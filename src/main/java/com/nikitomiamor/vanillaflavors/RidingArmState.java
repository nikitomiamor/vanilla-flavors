package com.nikitomiamor.vanillaflavors;

/** Lo implementa HumanoidModel: si la ultima vez que se dibujo el modelo completo estaba montado (bug MC-1349). */
public interface RidingArmState {
    boolean vanillaFlavors$lastPoseWasRiding();
}
