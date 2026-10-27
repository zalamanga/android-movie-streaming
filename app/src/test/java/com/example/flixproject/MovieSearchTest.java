package com.example.flixproject;

import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import Models.FilmDataModel;
import Models.FilmSearchResult;
import static org.junit.Assert.*;

public class MovieSearchTest {
    @Test public void missingRecordFieldsDoNotCrash() {
        assertFalse(MovieSearch.matches(null, null, "film"));
        assertTrue(MovieSearch.matches(null, "Example Actor", "actor"));
    }
    @Test public void matchesTitleOrCastWithoutCaseSensitivity() {
        assertTrue(MovieSearch.matches("Sample Movie", null, "MOVIE"));
        assertTrue(MovieSearch.matches("Other Film", "Sample Actor", " actor "));
        assertFalse(MovieSearch.matches("Other Film", "Sample Actor", "missing"));
    }
    @Test public void emptyQueryDoesNotReturnEveryFilm() {
        assertFalse(MovieSearch.matches("Sample Movie", "Sample Actor", "   "));
        assertFalse(MovieSearch.matches("Sample Movie", "Sample Actor", null));
    }

    @Test public void duplicateTitlesKeepDistinctFirebaseRecordIds() {
        FilmDataModel first = film("Same title");
        FilmDataModel second = film("Same title");
        List<FilmSearchResult> rows = Arrays.asList(
                new FilmSearchResult("record-one", first),
                new FilmSearchResult("record-two", second));
        List<FilmSearchResult> found = MovieSearch.find(rows, "same title", 15);
        assertEquals(2, found.size());
        assertEquals("record-one", found.get(0).getKey());
        assertEquals("record-two", found.get(1).getKey());
    }

    @Test public void resultLimitCountsMatchesAndSkipsMissingRows() {
        List<FilmSearchResult> rows = new ArrayList<>();
        rows.add(null);
        rows.add(new FilmSearchResult("unmatched", film("Different")));
        for (int i = 0; i < 20; i++) {
            rows.add(new FilmSearchResult("match-" + i, film("Sample " + i)));
        }
        List<FilmSearchResult> found = MovieSearch.find(rows, "sample", 3);
        assertEquals(3, found.size());
        assertEquals("match-0", found.get(0).getKey());
        assertEquals("match-2", found.get(2).getKey());
        assertTrue(MovieSearch.find(rows, "sample", 0).isEmpty());
    }

    @Test public void recordIdsCannotIntroduceAnotherDatabasePath() {
        assertTrue(FilmSearchResult.isValidKey("-SyntheticPushKey_123"));
        assertFalse(FilmSearchResult.isValidKey("record/another"));
        assertFalse(FilmSearchResult.isValidKey("record.with.dot"));
        assertFalse(FilmSearchResult.isValidKey("record\n"));
        assertFalse(FilmSearchResult.isValidKey(null));
    }

    private static FilmDataModel film(String title) {
        FilmDataModel film = new FilmDataModel();
        film.setFtitle(title);
        return film;
    }
}
