package com.apmuei.findmyrhythm.Model;

import androidx.annotation.NonNull;

import java.util.Date;
import java.util.HashSet;


public class SearchFilters {

    private String searchText;
    private boolean showPastEvents;
    private int minPrize;
    private int maxPrize;
    private HashSet<String> genres;

    public SearchFilters(String searchText, boolean showPastEvents, int minPrize, int maxPrize, HashSet<String> genres) {
        this.searchText = searchText;
        this.showPastEvents = showPastEvents;
        this.minPrize = minPrize;
        this.maxPrize = maxPrize;
        this.genres = genres;
    }

    // Copy constructor
    public SearchFilters(SearchFilters searchFilters) {
        this.searchText = searchFilters.getSearchText();
        this.showPastEvents = searchFilters.getShowPastEvents();
        this.minPrize = searchFilters.getMinPrize();
        this.maxPrize = searchFilters.getMaxPrize();
        this.genres = searchFilters.getGenres();
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

    public HashSet<String> getGenres() {
        return genres;
    }

    public void setGenres(HashSet<String> genres) {
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

    @NonNull
    @Override
    public String toString() {
        return "<" + searchText +"> "+ showPastEvents  + " "+ minPrize + "-" + maxPrize + " " + genres;
    }
}
