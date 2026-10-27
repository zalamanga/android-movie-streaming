package com.example.flixproject;

import java.util.Locale;
import java.util.ArrayList;
import java.util.List;
import Models.FilmSearchResult;

final class MovieSearch {
    private MovieSearch() {}

    static boolean matches(String title, String cast, String query) {
        String needle = normalize(query).trim();
        return !needle.isEmpty()
                && (normalize(title).contains(needle) || normalize(cast).contains(needle));
    }

    static List<FilmSearchResult> find(List<FilmSearchResult> rows, String query, int limit) {
        List<FilmSearchResult> selected = new ArrayList<>();
        if (limit <= 0) { return selected; }
        for (FilmSearchResult row : rows) {
            if (row != null && matches(row.getFilm().getFtitle(), row.getFilm().getFcast(), query)) {
                selected.add(row);
                if (selected.size() == limit) { break; }
            }
        }
        return selected;
    }

    private static String normalize(String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT);
    }
}
