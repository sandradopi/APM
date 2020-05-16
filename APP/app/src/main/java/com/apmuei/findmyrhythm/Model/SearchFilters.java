package com.apmuei.findmyrhythm.Model;

public class SearchFilters {
    private boolean showPastEvents;

    public SearchFilters(boolean showPastEvents) {
        this.showPastEvents = showPastEvents;
    }

    public boolean isShowPastEvents() {
        return showPastEvents;
    }

    public void setShowPastEvents(boolean showPastEvents) {
        this.showPastEvents = showPastEvents;
    }
}
