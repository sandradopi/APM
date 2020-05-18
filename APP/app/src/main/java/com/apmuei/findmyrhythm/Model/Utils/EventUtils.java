package com.apmuei.findmyrhythm.Model.Utils;

import com.apmuei.findmyrhythm.Model.Event;
import com.apmuei.findmyrhythm.Model.SearchFilters;

import java.util.Date;

public class EventUtils {

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
