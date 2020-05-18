package com.apmuei.findmyrhythm.Model;

import java.util.Date;

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

        return sameShowPast;
    }

    public static boolean applyFiltersToEvent(Event event, SearchFilters searchFilters) {
        boolean filtered;

        Date eventDate = event.getEventDate();
        Date currentDate = new Date();
        filtered = (!searchFilters.getShowPastEvents() && eventDate.before(currentDate));

        String searchString = searchFilters.getSearchText();
        String eventName = event.getName().toLowerCase();
        filtered = filtered || (!searchString.isEmpty() && !eventName.contains(searchString));

        return filtered;
    }

}
