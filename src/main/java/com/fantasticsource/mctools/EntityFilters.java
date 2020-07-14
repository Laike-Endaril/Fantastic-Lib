package com.fantasticsource.mctools;

import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.ExplicitPriorityQueue;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.commons.lang3.NotImplementedException;
import org.lwjgl.util.vector.Quaternion;

import java.util.ArrayList;
import java.util.Collections;

public class EntityFilters
{
    public static final int
            INCLUSION_MODE_ENTITY_POSITION = 0,
            INCLUSION_MODE_GEOMETRIC_CENTER = 1,
            INCLUSION_MODE_RECTANGULAR_PRISM_ANY = 2,
            INCLUSION_MODE_RECTANGULAR_PRISM_FULL = 3,
            INCLUSION_MODE_CYLINDER_ANY = 4,
            INCLUSION_MODE_CYLINDER_FULL = 5;

    private static final double DEFAULT_DISTRIBUTED_RAYTRACE_SPACING = 0.5;


    public static ArrayList<Entity> inWorld(World world, ArrayList<Entity> entitiesToCheck)
    {
        entitiesToCheck.removeIf(entity -> entity.world != world);
        return entitiesToCheck;
    }


    public static ArrayList<Entity> inCube(Vec3d origin, double halfSize, int inclusionMode, ArrayList<Entity> entitiesToCheck)
    {
        double top = origin.y + halfSize, bottom = origin.y - halfSize;
        double west = origin.x - halfSize, east = origin.x + halfSize;
        double north = origin.z - halfSize, south = origin.z + halfSize;

        switch (inclusionMode)
        {
            case INCLUSION_MODE_ENTITY_POSITION:
                entitiesToCheck.removeIf(entity -> entity.posX < west || entity.posX > east || entity.posZ < north || entity.posZ > south || entity.posY > top || entity.posY < bottom);
                break;

            case INCLUSION_MODE_GEOMETRIC_CENTER:
                entitiesToCheck.removeIf(entity ->
                {
                    if (entity.posX < west || entity.posX > east || entity.posZ < north || entity.posZ > south) return true;
                    double centerY = entity.posY + entity.height * 0.5;
                    return centerY > top || centerY < bottom;
                });
                break;

            case INCLUSION_MODE_RECTANGULAR_PRISM_ANY:
                entitiesToCheck.removeIf(entity ->
                {
                    double halfW = entity.width * 0.5;

                    double eWest = entity.posX - halfW, eEast = entity.posX + halfW;
                    if (eWest > east || eEast < west) return true;

                    double eNorth = entity.posZ - halfW, eSouth = entity.posZ + halfW;
                    if (eNorth > south || eSouth < north) return true;

                    double halfH = entity.height * 0.5;
                    double eTop = entity.posY + halfH, eBottom = entity.posY - halfH;
                    if (eTop < bottom || eBottom > top) return true;

                    return false;
                });
                break;

            case INCLUSION_MODE_RECTANGULAR_PRISM_FULL:
            case INCLUSION_MODE_CYLINDER_FULL:
                entitiesToCheck.removeIf(entity ->
                {
                    double halfW = entity.width * 0.5;

                    double eWest = entity.posX - halfW, eEast = entity.posX + halfW;
                    if (eWest < west || eEast > east) return true;

                    double eNorth = entity.posZ - halfW, eSouth = entity.posZ + halfW;
                    if (eNorth < north || eSouth > south) return true;

                    double halfH = entity.height * 0.5;
                    double eTop = entity.posY + halfH, eBottom = entity.posY - halfH;
                    if (eTop > top || eBottom < bottom) return true;

                    return false;
                });
                break;

            default:
                throw new NotImplementedException("Mode not implemented for this method: " + inclusionMode);
        }
        return entitiesToCheck;
    }


    public static ArrayList<Entity> inSphere(Vec3d origin, double radius, int inclusionMode, ArrayList<Entity> entitiesToCheck)
    {
        double squareRadius = radius * radius;

        switch (inclusionMode)
        {
            case INCLUSION_MODE_ENTITY_POSITION:
                entitiesToCheck.removeIf(entity -> origin.squareDistanceTo(entity.getPositionVector()) > squareRadius);
                break;

            case INCLUSION_MODE_GEOMETRIC_CENTER:
                entitiesToCheck.removeIf(entity -> origin.squareDistanceTo(entity.getPositionVector().addVector(0, entity.height * 0.5, 0)) > squareRadius);
                break;

            case INCLUSION_MODE_RECTANGULAR_PRISM_FULL:
                entitiesToCheck.removeIf(entity ->
                {
                    double halfW = entity.width * 0.5, halfH = entity.height * 0.5;
                    Vec3d geoCenter = entity.getPositionVector().addVector(0, halfH, 0);
                    return origin.squareDistanceTo(geoCenter.addVector(origin.x < geoCenter.x ? halfW : -halfW, origin.y < geoCenter.y ? halfH : -halfH, origin.z < geoCenter.z ? halfW : -halfW)) > squareRadius;
                });
                break;

            default:
                throw new NotImplementedException("Mode not implemented for this method: " + inclusionMode);
        }
        return entitiesToCheck;
    }


    public static ArrayList<Entity> inCone(Vec3d origin, float yaw, float pitch, double range, double angle, boolean LOS, ArrayList<Entity> entitiesToCheck)
    {
        return inCone(origin, yaw, pitch, range, angle, LOS, entitiesToCheck, DEFAULT_DISTRIBUTED_RAYTRACE_SPACING);
    }

    public static ArrayList<Entity> inCone(Vec3d origin, float yaw, float pitch, double range, double angle, boolean LOS, ArrayList<Entity> entitiesToCheck, double distributedRaytraceSpacing)
    {
        Vec3d coneAxisEnd = origin.add(Vec3d.fromPitchYaw(pitch, yaw).normalize().scale(range));
        double squareRange = origin.squareDistanceTo(coneAxisEnd), halfAngle = angle / 2;

        ExplicitPriorityQueue<Entity> queue = new ExplicitPriorityQueue<>(entitiesToCheck.size());

        if (!LOS)
        {
            for (Entity target : entitiesToCheck)
            {
                Vec3d targetCenter = target.getPositionVector().addVector(0, target.height * 0.5, 0);
                double squareDist = origin.squareDistanceTo(targetCenter);
                if (squareDist > squareRange) continue;

                if (MCTools.angleDifDeg(origin, coneAxisEnd, targetCenter) > halfAngle) continue;

                queue.add(target, squareDist);
            }
        }
        else
        {
            for (Entity target : entitiesToCheck)
            {
                Vec3d targetCenter = target.getPositionVector().addVector(0, target.height * 0.5, 0);
                double squareDist = origin.squareDistanceTo(targetCenter);
                if (squareDist > squareRange) continue;

                //Succeed if cone origin is within target sphere
                if (squareDist < Math.pow(target.width / 2, 2))
                {
                    queue.add(target, squareDist);
                    continue;
                }

                //Succeed if direct raytrace along cone axis hits
                if (ImprovedRayTracing.entityPenetration(target, origin, coneAxisEnd, true) >= 0)
                {
                    queue.add(target, squareDist);
                    continue;
                }

                //Don't do any more checks if our cone is actually a line
                if (halfAngle == 0) continue;

                //Succeed if direct raytrace along axis between sphere centers is within cone and hits
                double angleDif = MCTools.angleDifDeg(origin, coneAxisEnd, targetCenter);
                if (angleDif <= halfAngle && ImprovedRayTracing.entityPenetration(target, origin, targetCenter, true) >= 0)
                {
                    queue.add(target, squareDist);
                    continue;
                }


                //Final check: "shotgun" check (cone of distributed raytraces) (mostly useful for detection vs. large mobs)
                //Find evenly distributed points on evenly distributed subcones
                //Transform order is: yaw, pitch, roll (theta along circular intersection of cone and sphere), subConeAngle(angle of current cone)
                double distance = Math.sqrt(squareDist);
                double subConeStep = Tools.radtodeg(MCTools.TRIG_TABLE.arctan(distributedRaytraceSpacing / distance));
                int subConeCount = (int) (halfAngle / subConeStep);
                subConeStep = halfAngle / subConeCount;
                double subConeAngle = subConeStep;

                Vec3d pitchYaw = Vec3d.fromPitchYaw(0, yaw + 90);
                Quaternion qPitchAxis = new Quaternion((float) pitchYaw.x, (float) pitchYaw.y, (float) pitchYaw.z, 0);
                pitchYaw = Vec3d.fromPitchYaw(pitch, yaw);
                Quaternion qPitchYaw = new Quaternion((float) pitchYaw.x, (float) pitchYaw.y, (float) pitchYaw.z, 0);

                boolean stop = false;
                for (int cone = 0; cone < subConeCount; cone++)
                {
                    double radius = distance * MCTools.TRIG_TABLE.sin(Tools.degtorad(subConeAngle));
                    double rollStep = Math.PI * radius * 2 / distributedRaytraceSpacing;
                    int thetaStepCount = Tools.max((int) rollStep + 1, 4);
                    rollStep = Math.PI * 2 / thetaStepCount;
                    double roll = rollStep;
                    Quaternion theta0 = MCTools.rotatedQuaternion(qPitchYaw, qPitchAxis, Tools.degtorad(subConeAngle));

                    for (int thetaStepI = 0; thetaStepI < thetaStepCount; thetaStepI++)
                    {
                        //Final calc, using roll and subConeAngle


                        Quaternion qRotated = MCTools.rotatedQuaternion(theta0, qPitchYaw, roll);
                        qRotated.scale((float) distance);
                        Vec3d pos = new Vec3d(qRotated.x, qRotated.y, qRotated.z).add(origin);

                        if (ImprovedRayTracing.entityPenetration(target, origin, pos, true) > 0)
                        {
                            queue.add(target, squareDist);
                            stop = true;
                            break;
                        }

                        roll += rollStep;
                    }

                    if (stop) break;

                    subConeAngle += subConeStep;
                }


                //I feel a wall between us (or we're facing the wrong direction, etc)
            }
        }


        ArrayList<Entity> result = new ArrayList<>();
        Collections.addAll(result, queue.toArray(new Entity[0]));
        return result;
    }
}
