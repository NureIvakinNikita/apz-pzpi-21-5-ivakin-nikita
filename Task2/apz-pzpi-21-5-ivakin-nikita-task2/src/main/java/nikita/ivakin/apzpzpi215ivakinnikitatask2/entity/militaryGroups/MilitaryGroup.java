package nikita.ivakin.apzpzpi215ivakinnikitatask2.entity.militaryGroups;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class MilitaryGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private int personnelCount;
    private int ammo556x45ArCount;
    private int ammo545x39AkRpkCount;
    private int ammo762x39AkCount;
    private int ammo145KpvtCount;
    private int ammo40mmRpgCount;
    private int ammo40mmGpCount;
    private int ammo762PktCount;
    private int defensiveGrenadesCount;
    private int offensiveGrenadesCount;
    private int bodyArmorCount;
    private int helmetsCount;
    private int riflesCount;
    private int machineGunsCount;
    private int dryRationsCount;
    private int foodCount;
    private int tankCount;
    private int apcCount;
}