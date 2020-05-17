package com.apmuei.findmyrhythm.Model;

public class SearchFilters {
    private boolean showPastEvents;
    private String searchText = "";

    public SearchFilters(boolean showPastEvents) {
        this.showPastEvents = showPastEvents;
    }

    public boolean getShowPastEvents() {
        return showPastEvents;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        SearchFilters searchFilters = (SearchFilters) obj;
        boolean sameShowPast = (this.showPastEvents == (searchFilters.getShowPastEvents()));
        boolean sameSearchText = searchText.equals(searchFilters.getSearchText());

        return sameShowPast && sameSearchText;
    }

}
