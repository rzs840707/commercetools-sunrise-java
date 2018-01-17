package com.commercetools.sunrise.models.search.facetedsearch.viewmodels;

import java.util.List;

public abstract class FacetWithOptionsViewModel extends FacetViewModel {

    private String key;
    private boolean multiSelect;
    private boolean matchingAll;
    private List<FacetOption> limitedOptions;

    public FacetWithOptionsViewModel() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(final String key) {
        this.key = key;
    }

    public List<FacetOption> getLimitedOptions() {
        return limitedOptions;
    }

    public void setLimitedOptions(final List<FacetOption> limitedOptions) {
        this.limitedOptions = limitedOptions;
    }

    public boolean isMultiSelect() {
        return multiSelect;
    }

    public void setMultiSelect(final boolean multiSelect) {
        this.multiSelect = multiSelect;
    }

    public boolean isMatchingAll() {
        return matchingAll;
    }

    public void setMatchingAll(final boolean matchingAll) {
        this.matchingAll = matchingAll;
    }
}
