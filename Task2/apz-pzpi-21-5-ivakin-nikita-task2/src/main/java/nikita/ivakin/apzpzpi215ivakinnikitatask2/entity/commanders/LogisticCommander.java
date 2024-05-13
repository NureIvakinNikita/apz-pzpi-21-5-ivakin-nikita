package nikita.ivakin.apzpzpi215ivakinnikitatask2.entity.commanders;


import jakarta.persistence.*;
import lombok.*;
import nikita.ivakin.apzpzpi215ivakinnikitatask2.entity.militaryGroups.BrigadeGroup;
import nikita.ivakin.apzpzpi215ivakinnikitatask2.entity.militaryGroups.CompanyGroup;
import nikita.ivakin.apzpzpi215ivakinnikitatask2.entity.militaryGroups.LogisticCompany;

@Entity
@Table(name = "logistic_commander", schema = "project")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@AttributeOverride(name = "id", column = @Column(name = "logistic_commander_id"))
public class LogisticCommander extends Commander{


    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true,
            fetch = FetchType.LAZY)
    @JoinColumn(name = "logistic_company_id")
    private LogisticCompany logisticCompany;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH, CascadeType.REFRESH},
            fetch = FetchType.LAZY)
    @JoinColumn(name = "brigade_commander_id", unique = true)
    private BrigadeCommander brigadeCommander;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH, CascadeType.REFRESH},
            fetch = FetchType.LAZY)
    @JoinColumn(name = "brigade_group_id", unique = true)
    private BrigadeGroup brigadeGroup;
}