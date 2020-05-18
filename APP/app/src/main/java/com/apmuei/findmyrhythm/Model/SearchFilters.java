package com.apmuei.findmyrhythm.Model;

import java.util.ArrayList;
import java.util.Date;


public class SearchFilters {

    private boolean showPastEvents = false;
    private String searchText = "";
    private int minPrize = 0;
    private int maxPrize = 10000;
    private ArrayList<String> genres = new ArrayList<>();

    public SearchFilters(boolean showPastEvents) {
        this.showPastEvents = showPastEvents;
    }

    public SearchFilters(boolean showPastEvents, int minPrize, int maxPrize, ArrayList<String> genres) {
        this.showPastEvents = showPastEvents;
        this.minPrize = minPrize;
        this.maxPrize = maxPrize;
        this.genres = genres;
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

    public int getMinPrize() {
        return minPrize;
    }

    public void setMinPrize(int minPrize) {
        this.minPrize = minPrize;
    }

    public int getMaxPrize() {
        return maxPrize;
    }

    public void setMaxPrize(int maxPrize) {
        this.maxPrize = maxPrize;
    }

    public ArrayList<String> getGenres() {
        return genres;
    }

    public void setGenres(ArrayList<String> genres) {
        this.genres = genres;
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
        boolean sameMinMaxPrizes = (minPrize == searchFilters.getMinPrize()) &&
                (maxPrize == searchFilters.getMaxPrize());
        boolean sameGenres = genres.equals(searchFilters.getGenres());

        return sameShowPast && sameSearchText && sameMinMaxPrizes && sameGenres;
    }

    public static boolean applyFiltersToEvent(Event event, SearchFilters searchFilters) {
        boolean filtered;

        Date eventDate = event.getEventDate();
        Date currentDate = new Date();
        filtered = (!searchFilters.getShowPastEvents() && eventDate.before(currentDate));

        String searchString = searchFilters.getSearchText().toLowerCase();
        String eventName = event.getName().toLowerCase();
        filtered = filtered || (!searchString.isEmpty() && !eventName.contains(searchString));

        int eventPrice = Integer.parseInt(event.getPrice());
        filtered = filtered || (eventPrice > searchFilters.getMaxPrize());
        filtered = filtered || (eventPrice < searchFilters.getMinPrize());

        filtered = filtered || (! searchFilters.getGenres().contains(event.getGenre()));

        return filtered;
    }


}
