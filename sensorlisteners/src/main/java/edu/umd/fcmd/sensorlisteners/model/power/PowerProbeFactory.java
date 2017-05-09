package edu.umd.fcmd.sensorlisteners.model.power;

import android.content.Intent;
import android.os.BatteryManager;
import android.support.annotation.NonNull;

import javax.inject.Inject;

/**
 * Factory for creating PowerProbes and ChargingProbes
 */
public class PowerProbeFactory {

    /**
     * Explicity default constructor necessary for dependency injection with Dagger2
     */
    @SuppressWarnings("RedundantNoArgConstructor")
    @Inject
    public PowerProbeFactory() {
    }

    @SuppressWarnings("MethodMayBeStatic")
    @NonNull
    public ChargingProbe createChargingProbe(@NonNull Intent intent) {
        ChargingProbe chargingProbe = new ChargingProbe();
        chargingProbe.setDate(System.currentTimeMillis());

        switch (intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0)) {
            case 0:
                chargingProbe.setCharging(ChargingProbe.NONE);
                break;
            case BatteryManager.BATTERY_PLUGGED_AC:
                chargingProbe.setCharging(ChargingProbe.AC);
                break;
            case BatteryManager.BATTERY_PLUGGED_USB:
                chargingProbe.setCharging(ChargingProbe.USB);
                break;
            case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                chargingProbe.setCharging(ChargingProbe.WIRELESS);
                break;
            default:
                chargingProbe.setCharging(ChargingProbe.UNKNOWN);
                break;
        }
        return chargingProbe;
    }


    @SuppressWarnings("MethodMayBeStatic")
    @NonNull
    public PowerProbe createPowerProbe(@NonNull Intent intent) {
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        PowerProbe powerProbe = new PowerProbe();
        powerProbe.setDate(System.currentTimeMillis());
        powerProbe.setRemainingPower((double) level / (double) scale);
        powerProbe.setVoltage((double) intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1));
        powerProbe.setTemperature((double) intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1));
        powerProbe.setHealth((double) intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1));
        return powerProbe;
    }

}
