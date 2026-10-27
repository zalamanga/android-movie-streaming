package Models;

public final class FilmSearchResult {
    private final String key;
    private final FilmDataModel film;

    public FilmSearchResult(String key, FilmDataModel film) {
        if (!isValidKey(key) || film == null) {
            throw new IllegalArgumentException("A film result needs a Firebase record key and data");
        }
        this.key = key;
        this.film = film;
    }

    public String getKey() { return key; }
    public FilmDataModel getFilm() { return film; }

    public static boolean isValidKey(String value) {
        if (value == null || value.isEmpty()) { return false; }
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c <= 31 || c == 127 || ".#$[]/".indexOf(c) >= 0) { return false; }
        }
        return true;
    }
}
