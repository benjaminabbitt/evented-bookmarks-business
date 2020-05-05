package BusinessLogic.Bookmarks;
import name.benjaminabbitt.evented.core.Evented;
import name.benjaminabbitt.evented.bookmarks.Bookmarks;

public class DeletedBookmark implements Bookmark{

    @Override
    public Bookmark handle(Bookmarks.BookmarkDeleted bookmarkDeleted) {
        return null;
    }

    @Override
    public Evented.EventBook handle(Bookmarks.DeleteBookmark deleteBookmark) {
        return null;
    }

    @Override
    public Bookmark handle(Bookmarks.BookmarkCreated bookmarkCreated) {
        return null;
    }

    @Override
    public Evented.EventBook handle(Bookmarks.CreateBookmark createBookmark) {
        return null;
    }
}
