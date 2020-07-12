package com.fantasticsource.mctools;

import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.ExplicitPriorityQueue;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.lwjgl.util.vector.Quaternion;

public class EntityFilters
{
    private static final double DEFAULT_DISTRIBUTED_RAYTRACE_SPACING = 0.5;


    public static Entity[] inWorld(World world, Entity... entitiesToCheck)
    {

    }


    public static Entity[] withinCone(Vec3d origin, float yaw, float pitch, double range, double angle, boolean LOS, Entity... entitiesToCheck)
    {
        return withinCone(origin, yaw, pitch, range, angle, LOS, DEFAULT_DISTRIBUTED_RAYTRACE_SPACING, entitiesToCheck);
    }

    public static Entity[] withinCone(Vec3d origin, float yaw, float pitch, double range, double angle, boolean LOS, double distributedRaytraceSpacing, Entity... entitiesToCheck)
    {
        Vec3d coneAxisEnd = origin.add(Vec3d.fromPitchYaw(pitch, yaw).normalize().scale(range));
        double squareRange = origin.squareDistanceTo(coneAxisEnd), halfAngle = angle / 2;

        ExplicitPriorityQueue<Entity> queue = new ExplicitPriorityQueue<>(entitiesToCheck.length);

        if (!LOS)
        {
            for (Entity target : entitiesToCheck)
            {
                Vec3d targetCenter = target.getPositionVector().addVector(0, target.height / 2, 0);
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
                Vec3d targetCenter = target.getPositionVector().addVector(0, target.height / 2, 0);
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

        return queue.toArray(new Entity[0]);
    }
}
