package com.fantasticsource.mctools.particles;

@FunctionalInterface
public interface PathedParticleFactory
{
    PathedParticle create(Object... args);
}
