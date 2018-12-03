package nl.blackstardlb.petstore.viewmodels;

import javax.inject.Inject;

public class MainActivityViewModel extends BaseViewModel {
    private Integer previousActionId;
    private String currentTag;

    @Inject
    public MainActivityViewModel() {
    }

    public Integer getPreviousActionId() {
        return previousActionId;
    }

    public void setPreviousActionId(Integer previousActionId) {
        this.previousActionId = previousActionId;
    }

    public String getCurrentTag() {
        return currentTag;
    }

    public void setCurrentTag(String currentTag) {
        this.currentTag = currentTag;
    }
}
