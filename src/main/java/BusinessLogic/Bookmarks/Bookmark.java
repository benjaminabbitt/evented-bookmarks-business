package BusinessLogic.Bookmarks;


import name.benjaminabbitt.evented.bookmarks.Bookmarks;
import name.benjaminabbitt.evented.core.Evented;

public interface Bookmark {
    Bookmark handle(Bookmarks.BookmarkDeleted bookmarkDeleted);

    Evented.EventBook handle(Bookmarks.DeleteBookmark deleteBookmark);

    Bookmark handle(Bookmarks.BookmarkCreated bookmarkCreated);

    Evented.EventBook handle(Bookmarks.CreateBookmark createBookmark);
}
