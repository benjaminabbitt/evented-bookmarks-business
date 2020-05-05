package BusinessLogic;

import BusinessLogic.Bookmarks.Bookmark;
import BusinessLogic.Bookmarks.NotCreatedBookmark;
import com.google.protobuf.InvalidProtocolBufferException;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import name.benjaminabbitt.evented.bookmarks.Bookmarks;
import name.benjaminabbitt.evented.business.BusinessLogicGrpc;
import name.benjaminabbitt.evented.core.Evented;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class BookmarkServer {
    private static final Logger logger = Logger.getLogger(BookmarkServer.class.getName());
    private final Server server;

    public BookmarkServer(int port) {
        this(ServerBuilder.forPort(port));
    }

    public BookmarkServer(ServerBuilder<?> serverBuilder) {
        this.server = serverBuilder.addService(new BookmarkService()).build();
    }

    public void start() throws IOException {
        this.server.start();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                try {
                    BookmarkServer.this.stop();
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
                System.err.println("*** server shut down");
            }
        });
    }

    /**
     * Stop serving requests and shutdown resources.
     */
    public void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    private static class BookmarkService extends BusinessLogicGrpc.BusinessLogicImplBase {
        /**
         * @param request
         * @param responseObserver
         */
        @Override
        public void handle(Evented.ContextualCommand request, StreamObserver<Evented.EventBook> responseObserver) {
            assert request != null;
            request.getEvents().getPagesList().forEach(ea -> {
                try {
                    this.bookmark = this.dispatchEvent(ea);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            request.getCommand().getPagesList().forEach(ea -> {
                try {
                    this.dispatchCommand(ea);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        private Bookmark bookmark;

        public BookmarkService() {
            this.bookmark = new NotCreatedBookmark();
        }

        private Bookmark dispatchEvent(Evented.EventPage page) throws Exception {
            try {
                if (page.getEvent().is(Bookmarks.BookmarkCreated.class)) {
                    return this.bookmark.handle(page.getEvent().unpack(Bookmarks.BookmarkCreated.class));
                } else if (page.getEvent().is(Bookmarks.BookmarkDeleted.class)) {
                    return this.bookmark.handle(page.getEvent().unpack(Bookmarks.BookmarkDeleted.class));
                } else {
                    throw new Exception("TODO");
                }
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
            throw new Exception("TODO");
        }

        private Evented.EventBook dispatchCommand(Evented.CommandPage page) throws Exception {
            try {
                if (page.getCommand().is(Bookmarks.CreateBookmark.class)) {
                    return this.bookmark.handle(page.getCommand().unpack(Bookmarks.CreateBookmark.class));
                } else if (page.getCommand().is(Bookmarks.DeleteBookmark.class)) {
                    return this.bookmark.handle(page.getCommand().unpack(Bookmarks.DeleteBookmark.class));
                }
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
            throw new Exception("TODO");
        }
    }

    public static void main(String[] args) throws Exception {
        BookmarkServer server = new BookmarkServer(8080);
        server.start();
        server.blockUntilShutdown();
    }
}