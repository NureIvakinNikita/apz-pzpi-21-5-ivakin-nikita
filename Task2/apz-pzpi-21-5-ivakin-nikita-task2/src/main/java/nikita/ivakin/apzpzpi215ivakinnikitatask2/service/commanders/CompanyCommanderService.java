package nikita.ivakin.apzpzpi215ivakinnikitatask2.service.commanders;


import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nikita.ivakin.apzpzpi215ivakinnikitatask2.dto.ResourcesRequestDTO;
import nikita.ivakin.apzpzpi215ivakinnikitatask2.dto.groups.CompanyGroupDTO;
import nikita.ivakin.apzpzpi215ivakinnikitatask2.dto.groups.PlatGroupDTO;
import nikita.ivakin.apzpzpi215ivakinnikitatask2.entity.GivenResources;
import nikita.ivakin.apzpzpi215ivakinnikitatask2.entity.ResourcesRequest;
import nikita.ivakin.apzpzpi215ivakinnikitatask2.entity.ResourcesUpdateResponse;
import nikita.ivakin.apzpzpi215ivakinnikitatask2.entity.SupplyRequest;
import nikita.ivakin.apzpzpi215ivakinnikitatask2.entity.commanders.CompanyCommander;
import nikita.ivakin.apzpzpi215ivakinnikitatask2.entity.commanders.PlatCommander;
import nikita.ivakin.apzpzpi215ivakinnikitatask2.entity.militaryGroups.CompanyGroup;
import nikita.ivakin.apzpzpi215ivakinnikitatask2.entity.militaryGroups.PlatGroup;
import nikita.ivakin.apzpzpi215ivakinnikitatask2.enums.ResourcesType;
import nikita.ivakin.apzpzpi215ivakinnikitatask2.enums.Role;
import nikita.ivakin.apzpzpi215ivakinnikitatask2.enums.Status;
import nikita.ivakin.apzpzpi215ivakinnikitatask2.exceptions.*;
import nikita.ivakin.apzpzpi215ivakinnikitatask2.repository.commanders.CompanyCommanderRepository;
import nikita.ivakin.apzpzpi215ivakinnikitatask2.service.GivenResourcesService;
import nikita.ivakin.apzpzpi215ivakinnikitatask2.service.groups.CompanyGroupService;
import nikita.ivakin.apzpzpi215ivakinnikitatask2.service.groups.PlatGroupService;
import nikita.ivakin.apzpzpi215ivakinnikitatask2.service.requests.ResourcesRequestService;
import nikita.ivakin.apzpzpi215ivakinnikitatask2.service.requests.SupplyRequestService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class CompanyCommanderService {


    private final CompanyCommanderRepository companyCommanderRepository;
    private final CompanyGroupService companyGroupService;
    private final PlatGroupService platGroupService;
    private final PlatCommanderService platCommanderService;
    private final ResourcesRequestService resourcesRequestService;
    private final SupplyRequestService supplyRequestService;
    private final GivenResourcesService givenResourcesService;



    public CompanyCommander getAuthenticatedCompanyCommander() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String companyCommanderEmail = authentication.getName();
            return findCompanyCommanderByEmail(companyCommanderEmail);
        } catch (Exception e) {
            throw new CommanderAuthenticationException("Error in getting authenticated company commander.");
        }
    }

    public boolean createPlat(PlatGroupDTO platGroupDTO) {
        CompanyCommander companyCommander = getAuthenticatedCompanyCommander();
        return platGroupService.createPlatGroup(platGroupDTO, companyCommander);
    }

    public boolean assignPlatCommander(Integer platCommanderId, Integer platGroupId) {
        CompanyCommander companyCommander = getAuthenticatedCompanyCommander();
        if (companyCommander.getCompanyGroup() == null) {
            throw new CommanderDoesNotAssignedException("Commander isn't assigned to any company, assigning a plat commander isn't possible.");
        }
        PlatCommander platCommander = platCommanderService.findPlatCommanderById(platCommanderId);
        PlatGroup platGroup = platGroupService.findPlatGroupById(platGroupId);
        givenResourcesService.assignCommander(platCommanderId, platGroupId, companyCommander.getBrigadeCommanderId(), Role.PLAT_COMMANDER);

        platGroup.setPlatCommanderId(platCommander);
        platCommander.setPlatGroup(platGroup);
        platCommander.setCompanyCommander(companyCommander);
        platCommander.setCompanyGroup(companyCommander.getCompanyGroup());
        platCommander.setBrigadeCommanderId(companyCommander.getBrigadeCommanderId());
        try {
            platCommanderService.save(platCommander);
            platGroupService.save(platGroup);
        } catch (Exception e) {
            throw new CommanderAssigningException("Something went wrong in assigning PlatCommander.");
        }

        return true;
    }

    public CompanyCommander findCompanyCommanderById(Integer id) {
        Optional<CompanyCommander> tempCompCom = companyCommanderRepository.findCompanyCommanderById(id);
        if (tempCompCom.isPresent()) {
            return tempCompCom.get();
        } else {
            throw new CommanderNotFoundException("Error company commander with id" + id + " doesn't exist.");
        }
    }

    public CompanyCommander findCompanyCommanderByEmail(String email) {
        Optional<CompanyCommander> tempCompCom = companyCommanderRepository.findCompanyCommanderByEmail(email);
        if (tempCompCom.isPresent()) {
            return tempCompCom.get();
        } else {
            throw new CommanderNotFoundException("Error company commander with email" + email  + " doesn't exist.");
        }
    }

    @Transactional
    public void save(CompanyCommander companyCommander) {
        companyCommanderRepository.save(companyCommander);
    }

    public boolean askForResources(ResourcesRequestDTO resourcesRequestDTO) {
        CompanyCommander companyCommander = getAuthenticatedCompanyCommander();
        ResourcesRequest resourcesRequest = ResourcesRequest.builder()
                .commanderId(companyCommander.getId())
                .militaryGroupId(companyCommander.getCompanyGroup().getId())
                .roleOfCommander(companyCommander.getRole())
                .ammo40mmGpCount(resourcesRequestDTO.getAmmo40mmGpCount())
                .ammo40mmRpgCount(resourcesRequestDTO.getAmmo40mmRpgCount())
                .ammo145KpvtCount(resourcesRequestDTO.getAmmo145KpvtCount())
                .ammo545x39AkRpkCount(resourcesRequestDTO.getAmmo545x39AkRpkCount())
                .ammo556x45ArCount(resourcesRequestDTO.getAmmo556x45ArCount())
                .ammo762PktCount(resourcesRequestDTO.getAmmo762PktCount())
                .ammo762x39AkCount(resourcesRequestDTO.getAmmo762x39AkCount())
                .offensiveGrenadesCount(resourcesRequestDTO.getOffensiveGrenadesCount())
                .defensiveGrenadesCount(resourcesRequestDTO.getDefensiveGrenadesCount())
                .riflesCount(resourcesRequestDTO.getRiflesCount())
                .bodyArmorCount(resourcesRequestDTO.getBodyArmorCount())
                .helmetsCount(resourcesRequestDTO.getHelmetsCount())
                .apcCount(resourcesRequestDTO.getApcCount())
                .tankCount(resourcesRequestDTO.getTankCount())
                .build();
        SupplyRequest supplyRequest = SupplyRequest.builder()
                .brigadeCommanderId(companyCommander.getBrigadeCommanderId())
                .seniorMilitaryGroupId(companyCommander.getBattalionGroup().getId())
                .commanderId(companyCommander.getId())
                .militaryGroupId(companyCommander.getCompanyGroup().getId())
                .roleOfCommander(companyCommander.getRole())
                .dateOfRequest(LocalDate.now())
                .status(Status.NOT_PROCESSED)
                .build();
        try {
            resourcesRequestService.save(resourcesRequest);
        } catch (Exception e) {
            throw new ResourcesRequestCreationException("Something went wrong in creation resources request in company commander method ask for resources.");
        }
        resourcesRequest = resourcesRequestService.findResourcesRequestByCommanderIdAndMilitaryGroupId(companyCommander.getId(), companyCommander.getCompanyGroup().getId());
        supplyRequest.setResourcesRequestId(resourcesRequest);
        try {
            supplyRequestService.save(supplyRequest);
        } catch (Exception e) {
            throw new SupplyRequestCreationException("Something went wrong in creation supply request in company commander method ask for resources.");
        }
        return true;
    }

    public List<SupplyRequest> getCompanyRequests() {
        CompanyCommander companyCommander = getAuthenticatedCompanyCommander();
        return supplyRequestService.getSupplyRequestsForCompanyByCompanyId(companyCommander.getCompanyGroup().getId(), companyCommander.getRole());
    }

    public List<SupplyRequest> getPlatsRequests() {
        CompanyCommander companyCommander = getAuthenticatedCompanyCommander();
        return supplyRequestService.getSupplyRequestsForPlatsByCompanyId(companyCommander.getCompanyGroup().getId(), Role.PLAT_COMMANDER);
    }

    public boolean validateResources(CompanyGroup companyGroup) {
        CompanyCommander companyCommander = getAuthenticatedCompanyCommander();
        GivenResources givenResources = givenResourcesService.getGivenResources(
                companyCommander.getId(), companyCommander.getCompanyGroup().getId(), companyCommander.getRole(), companyCommander.getBrigadeCommanderId(), ResourcesType.FOR_PERFORMING_A_MISSION
        );
        return companyGroup.getAmmo762PktCount() >= givenResources.getAmmo762PktCount() / 4 && companyGroup.getAmmo556x45ArCount() >= givenResources.getAmmo556x45ArCount() / 4
                && companyGroup.getAmmo545x39AkRpkCount() >= givenResources.getAmmo545x39AkRpkCount() / 4 && companyGroup.getAmmo762x39AkCount() >= givenResources.getAmmo762x39AkCount() / 4
                && companyGroup.getAmmo145KpvtCount() >= givenResources.getAmmo145KpvtCount() / 4 && companyGroup.getAmmo40mmGpCount() >= givenResources.getAmmo40mmGpCount() / 4
                && companyGroup.getAmmo40mmRpgCount() >= givenResources.getAmmo40mmRpgCount() / 4 && companyGroup.getBodyArmorCount() >= givenResources.getBodyArmorCount()
                && companyGroup.getHelmetsCount() >= givenResources.getHelmetsCount() && companyGroup.getApcCount() >= givenResources.getApcCount();
    }

    //add throw
    public ResourcesUpdateResponse updateCompanyResources(CompanyGroupDTO companyGroupDTO) {
        try {
            CompanyGroup companyGroup = companyGroupService.updateCompanyResources(companyGroupDTO);
            boolean validationResult = !validateResources(companyGroup);
            boolean updateResult = companyGroup != null;
            return new ResourcesUpdateResponse(updateResult, validationResult);
        } catch (Exception e) {
            throw new MilitaryGroupUpdateException("Something went wrong  in updating company resources.");
        }
    }

    public ResourcesUpdateResponse sendResourcesToPlat(SupplyRequest supplyRequest) {
        try {
            CompanyCommander companyCommander = getAuthenticatedCompanyCommander();
            if (supplyRequest.getBrigadeCommanderId().equals(companyCommander.getBrigadeCommanderId()) && supplyRequest.getSeniorMilitaryGroupId().equals(companyCommander.getCompanyGroup().getId())
                    && supplyRequest.getRoleOfCommander().equals(Role.COMPANY_COMMANDER)){
                PlatGroup platGroup = platGroupService.findPlatGroupById(supplyRequest.getMilitaryGroupId());
                return allocateResources(supplyRequest.getResourcesRequestId(), companyCommander.getCompanyGroup(), companyCommander, platGroup);
            }
        } catch (Exception e) {
            throw new CommanderSendingResourcesException("Error in sending resources to plat.");
        }

        return new ResourcesUpdateResponse(false, false);
    }

    public ResourcesUpdateResponse allocateResources(ResourcesRequest resourcesRequest, CompanyGroup companyGroup, CompanyCommander companyCommander, PlatGroup platGroup) {
        boolean needForSupply = givenResourcesService.allocateResources(resourcesRequest, companyGroup, platGroup,
                platGroup.getPlatCommanderId().getId(), platGroup.getPlatCommanderId().getRole(),
                platGroup.getPlatCommanderId().getBrigadeCommanderId());
        return new ResourcesUpdateResponse(true, needForSupply);
    }


    public List<PlatGroup> getCompanyPlatGroups() {
        CompanyCommander companyCommander = getAuthenticatedCompanyCommander();
        return platGroupService.findPlatGroupsByCompanyGroup(companyCommander.getCompanyGroup());
    }

    public CompanyGroupDTO getCompanyGroup() {
        CompanyCommander companyCommander = getAuthenticatedCompanyCommander();
        CompanyGroup companyGroup = companyGroupService.findCompanyGroupByCompanyCommander(companyCommander);
        return companyGroupService.mapCompanyGroupToDTO(companyGroup);
    }
}

