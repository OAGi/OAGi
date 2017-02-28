package org.oagi.srt.web.jsf.beans.cc;

import org.oagi.srt.repository.*;
import org.oagi.srt.repository.entity.*;
import org.oagi.srt.service.CoreComponentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.web.context.WebApplicationContext.SCOPE_SESSION;

@Controller
@Scope(SCOPE_SESSION)
@ManagedBean
@SessionScoped
@Transactional(readOnly = true)
public class CoreComponentBean extends AbstractCoreComponentBean {

    @Autowired
    private AggregateCoreComponentRepository accRepository;
    @Autowired
    private AssociationCoreComponentRepository asccRepository;
    @Autowired
    private AssociationCoreComponentPropertyRepository asccpRepository;
    @Autowired
    private BasicCoreComponentRepository bccRepository;
    @Autowired
    private BasicCoreComponentPropertyRepository bccpRepository;
    @Autowired
    private DataTypeRepository dataTypeRepository;
    @Autowired
    private CoreComponentService coreComponentService;

    private String type = "ACC";
    private List<CoreComponentState> selectedStates;

    private String searchText;

    @PostConstruct
    public void init() {
        selectedStates = Arrays.asList(CoreComponentState.Editing);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void onTypeChange() {

    }

    public String[] getSelectedStates() {
        if (selectedStates == null || selectedStates.isEmpty()) {
            return new String[0];
        }

        String[] selectedStateStrings = new String[selectedStates.size()];
        int index = 0;
        for (CoreComponentState coreComponentState : selectedStates) {
            selectedStateStrings[index++] = coreComponentState.toString();
        }
        return selectedStateStrings;
    }

    public void setSelectedStates(String[] selectedStates) {
        if (selectedStates != null && selectedStates.length > 0) {
            this.selectedStates = new ArrayList();
            for (String selectedState : selectedStates) {
                this.selectedStates.add(CoreComponentState.valueOf(selectedState));
            }
        }
    }

    public void onStateChange() {
        reset();
    }

    public List<AggregateCoreComponent> getAccList() {
        List<AggregateCoreComponent> accList;
        if (selectedStates == null || selectedStates.isEmpty()) {
            accList = accRepository.findAllByRevisionNum(0);
        } else {
            accList = accRepository.findAllByRevisionNumAndStates(0, selectedStates);
        }

        if (searchText != null) {
            return accList.stream()
                    .filter(e -> e.getObjectClassTerm().toLowerCase().contains(searchText.toLowerCase()))
                    .collect(Collectors.toList());
        } else {
            return accList;
        }
    }

    public List<AssociationCoreComponent> getAsccList() {
        List<AssociationCoreComponent> asccList;
        if (selectedStates == null || selectedStates.isEmpty()) {
            asccList = asccRepository.findAllByRevisionNum(0);
        } else {
            asccList = asccRepository.findAllByRevisionNumAndStates(0, selectedStates);
        }

        if (searchText != null) {
            return asccList.stream()
                    .filter(e -> e.getDen().toLowerCase().contains(searchText.toLowerCase()))
                    .collect(Collectors.toList());
        } else {
            return asccList;
        }
    }

    public List<AssociationCoreComponentProperty> getAsccpList() {
        List<AssociationCoreComponentProperty> asccpList;
        if (selectedStates == null || selectedStates.isEmpty()) {
            asccpList = asccpRepository.findAllByRevisionNum(0);
        } else {
            asccpList = asccpRepository.findAllByRevisionNumAndStates(0, selectedStates);
        }

        if (searchText != null) {
            return asccpList.stream()
                    .filter(e -> e.getPropertyTerm().toLowerCase().contains(searchText.toLowerCase()))
                    .collect(Collectors.toList());
        } else {
            return asccpList;
        }
    }

    public List<BasicCoreComponent> getBccList() {
        List<BasicCoreComponent> bccList;
        if (selectedStates == null || selectedStates.isEmpty()) {
            bccList = bccRepository.findAllByRevisionNum(0);
        } else {
            bccList = bccRepository.findAllByRevisionNumAndStates(0, selectedStates);
        }

        if (searchText != null) {
            return bccList.stream()
                    .filter(e -> e.getDen().toLowerCase().contains(searchText.toLowerCase()))
                    .collect(Collectors.toList());
        } else {
            return bccList;
        }
    }

    public List<BasicCoreComponentProperty> getBccpList() {
        List<BasicCoreComponentProperty> bccpList;
        if (selectedStates == null || selectedStates.isEmpty()) {
            bccpList = bccpRepository.findAllByRevisionNum(0);
        } else {
            bccpList = bccpRepository.findAllByRevisionNumAndStates(0, selectedStates);
        }

        if (searchText != null) {
            return bccpList.stream()
                    .filter(e -> e.getPropertyTerm().toLowerCase().contains(searchText.toLowerCase()))
                    .collect(Collectors.toList());
        } else {
            return bccpList;
        }
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public List<String> completeInput(String query) {
        String q = (query != null) ? query.trim() : null;

        if (StringUtils.isEmpty(q)) {
            switch (getType()) {
                case "ACC":
                    return getAccList().stream()
                            .map(e -> e.getObjectClassTerm())
                            .collect(Collectors.toList());
                case "ASCC":
                    return getAsccList().stream()
                            .map(e -> e.getDen())
                            .collect(Collectors.toList());
                case "ASCCP":
                    return getAsccpList().stream()
                            .map(e -> e.getPropertyTerm())
                            .collect(Collectors.toList());
                case "BCC":
                    return getBccList().stream()
                            .map(e -> e.getDen())
                            .collect(Collectors.toList());
                case "BCCP":
                    return getBccpList().stream()
                            .map(e -> e.getPropertyTerm())
                            .collect(Collectors.toList());
                default:
                    throw new IllegalStateException();
            }
        } else {
            String[] split = q.split(" ");

            switch (getType()) {
                case "ACC":
                    return getAccList().stream()
                            .map(e -> e.getObjectClassTerm())
                            .distinct()
                            .filter(e -> {
                                e = e.toLowerCase();
                                for (String s : split) {
                                    if (!e.contains(s.toLowerCase())) {
                                        return false;
                                    }
                                }
                                return true;
                            })
                            .collect(Collectors.toList());
                case "ASCC":
                    return getAsccList().stream()
                            .map(e -> e.getDen())
                            .distinct()
                            .filter(e -> {
                                e = e.toLowerCase();
                                for (String s : split) {
                                    if (!e.contains(s.toLowerCase())) {
                                        return false;
                                    }
                                }
                                return true;
                            })
                            .collect(Collectors.toList());
                case "ASCCP":
                    return getAsccpList().stream()
                            .map(e -> e.getPropertyTerm())
                            .distinct()
                            .filter(e -> {
                                e = e.toLowerCase();
                                for (String s : split) {
                                    if (!e.contains(s.toLowerCase())) {
                                        return false;
                                    }
                                }
                                return true;
                            })
                            .collect(Collectors.toList());
                case "BCC":
                    return getBccList().stream()
                            .map(e -> e.getDen())
                            .distinct()
                            .filter(e -> {
                                e = e.toLowerCase();
                                for (String s : split) {
                                    if (!e.contains(s.toLowerCase())) {
                                        return false;
                                    }
                                }
                                return true;
                            })
                            .collect(Collectors.toList());
                case "BCCP":
                    return getBccpList().stream()
                            .map(e -> e.getPropertyTerm())
                            .distinct()
                            .filter(e -> {
                                e = e.toLowerCase();
                                for (String s : split) {
                                    if (!e.contains(s.toLowerCase())) {
                                        return false;
                                    }
                                }
                                return true;
                            })
                            .collect(Collectors.toList());
                default:
                    throw new IllegalStateException();
            }
        }
    }

    public void search() {
        reset();
    }

    private void reset() {
    }

    @Transactional
    public String createACC() {
        User requester = getCurrentUser();
        AggregateCoreComponent acc = coreComponentService.newAggregateCoreComponent(requester);

        return "/views/core_component/acc_details.xhtml?accId=" + acc.getAccId() + "&faces-redirect=true";
    }

    @Transactional
    public String createBCCP(long bdtId) {
        User requester = getCurrentUser();
        DataType bdt = dataTypeRepository.findOne(bdtId);
        BasicCoreComponentProperty bccp = coreComponentService.newBasicCoreComponentProperty(requester, bdt);

        return "/views/core_component/bccp_details.xhtml?bccpId=" + bccp.getBccpId() + "&faces-redirect=true";
    }
}
