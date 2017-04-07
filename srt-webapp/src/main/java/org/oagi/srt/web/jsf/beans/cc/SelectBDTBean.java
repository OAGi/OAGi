package org.oagi.srt.web.jsf.beans.cc;

import org.oagi.srt.repository.DataTypeRepository;
import org.oagi.srt.repository.entity.BasicCoreComponentProperty;
import org.oagi.srt.repository.entity.DataType;
import org.oagi.srt.repository.entity.User;
import org.oagi.srt.service.CoreComponentService;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.oagi.srt.repository.entity.CoreComponentState.Published;
import static org.oagi.srt.repository.entity.DataTypeType.BusinessDataType;

@Controller
@Scope("view")
@ManagedBean
@ViewScoped
@Transactional(readOnly = true)
public class SelectBDTBean extends AbstractCoreComponentBean {

    @Autowired
    private DataTypeRepository dataTypeRepository;
    @Autowired
    private CoreComponentService coreComponentService;

    private List<DataType> bdtList;
    private String qualifierDataTypeTerm;
    private DataType selectedBDT;

    @PostConstruct
    public void init() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        Map<String, String> requestParameterMap = externalContext.getRequestParameterMap();
        type = requestParameterMap.get("type");
        states = requestParameterMap.get("states");

        setBdtList(allBDTs());
    }

    private List<DataType> allBDTs() {
        return dataTypeRepository.findAll(new Sort(Sort.Direction.DESC, "lastUpdateTimestamp")).stream()
                .filter(e -> BusinessDataType == e.getType())
                .filter(e -> Published == e.getState())
                .collect(Collectors.toList());
    }

    public List<DataType> getBdtList() {
        return bdtList;
    }

    public void setBdtList(List<DataType> bdtList) {
        this.bdtList = bdtList;
    }

    public String getQualifierDataTypeTerm() {
        return qualifierDataTypeTerm;
    }

    public void setQualifierDataTypeTerm(String qualifierDataTypeTerm) {
        this.qualifierDataTypeTerm = qualifierDataTypeTerm;
    }

    public DataType getSelectedBDT() {
        return selectedBDT;
    }

    public void setSelectedBDT(DataType selectedBDT) {
        this.selectedBDT = selectedBDT;
    }

    public void onBDTSelect(SelectEvent event) {
        setSelectedBDT((DataType) event.getObject());
    }

    public void onBDTUnselect(UnselectEvent event) {
        setSelectedBDT(null);
    }

    public List<String> completeInput(String query) {
        String q = (query != null) ? query.trim() : null;

        if (StringUtils.isEmpty(q)) {
            return allBDTs().stream()
                    .map(e -> toCompleteInputString(e))
                    .collect(Collectors.toList());
        } else {
            String[] split = q.split(" ");

            return allBDTs().stream()
                    .map(e -> toCompleteInputString(e))
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
        }
    }

    public void search() {
        String qualifierDataTypeTerm = getQualifierDataTypeTerm();
        if (StringUtils.isEmpty(qualifierDataTypeTerm)) {
            setBdtList(allBDTs());
        } else {
            setBdtList(
                    allBDTs().stream()
                            .filter(e -> (toCompleteInputString(e)).toLowerCase().contains(qualifierDataTypeTerm.toLowerCase()))
                            .collect(Collectors.toList())
            );
        }
    }

    private String toCompleteInputString(DataType dataType) {
        StringBuilder sb = new StringBuilder();
        String qualifier = dataType.getQualifier();
        if (!StringUtils.isEmpty(qualifier)) {
            sb.append(qualifier).append(" ");
        }
        String dataTypeTerm = dataType.getDataTypeTerm();
        sb.append(dataTypeTerm);
        return sb.toString();
    }

    // To support 'back' button to go back 'list' page.
    private String type;
    private String states;

    public String back() {
        return "/views/core_component/list.jsf?type=" + type + "&states= " + states + "&faces-redirect=true";
    }

    @Transactional
    public String createBCCP() {
        User requester = getCurrentUser();
        DataType bdt = getSelectedBDT();
        BasicCoreComponentProperty bccp = coreComponentService.newBasicCoreComponentProperty(requester, bdt);

        return "/views/core_component/bccp_details.xhtml?bccpId=" + bccp.getBccpId() + "&type=" + type + "&states= " + states + "&faces-redirect=true";
    }

}
