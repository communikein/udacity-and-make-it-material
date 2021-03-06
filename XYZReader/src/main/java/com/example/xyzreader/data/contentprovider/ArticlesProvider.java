
package com.example.xyzreader.data.contentprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.example.xyzreader.data.database.ArticlesDao;
import com.example.xyzreader.data.model.Article;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.Lazy;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasContentProviderInjector;

public class ArticlesProvider extends ContentProvider implements
        HasContentProviderInjector {

    public static final String AUTHORITY = "com.example.xyzreader";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    private static final int CODE_ARTICLES_DIR = 1000;
    private static final int CODE_ARTICLES_ITEM = 1001;

	private static final UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    @Inject
    DispatchingAndroidInjector<ContentProvider> dispatchingAndroidInjector;

    @Inject
    Lazy<ArticlesDao> articlesDao;

	static {
        MATCHER.addURI(AUTHORITY, ArticleContract.ArticleEntry.TABLE_NAME, CODE_ARTICLES_DIR);
        MATCHER.addURI(AUTHORITY, ArticleContract.ArticleEntry.TABLE_NAME + "/#", CODE_ARTICLES_ITEM);
	}

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
	public String getType(@NonNull Uri uri) {
		return null;
	}

	@Override
	public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        final Context context = getContext();
        if (context == null) return null;

        Cursor cursor;
        long id;
        final int code = MATCHER.match(uri);
        switch (code) {
            case CODE_ARTICLES_DIR:
                cursor = articlesDao.get().getArticlesAsCursor();
                cursor.setNotificationUri(context.getContentResolver(), uri);
                break;

            case CODE_ARTICLES_ITEM:
                id = Long.parseLong(uri.getQueryParameter(ArticleContract.ArticleEntry.COLUMN_ID));
                cursor = articlesDao.get().getArticleAsCursor(id);
                cursor.setNotificationUri(context.getContentResolver(), uri);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        return cursor;
	}

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final Context context = getContext();
        if (context == null) {
            return 0;
        }

        switch (MATCHER.match(uri)) {
            case CODE_ARTICLES_DIR:
                final ArrayList<Article> articles = new ArrayList<>();
                for (ContentValues article : values)
                    articles.add(Article.fromContentValues(article));

                return articlesDao.get().addArticles(articles).length;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
	public Uri insert(@NonNull Uri uri, ContentValues values) {
        final Context context = getContext();
        if (context == null) {
            return null;
        }

        long id;
        switch (MATCHER.match(uri)) {
            case CODE_ARTICLES_ITEM:
                id = articlesDao.get().addArticle(Article.fromContentValues(values));
                context.getContentResolver().notifyChange(uri, null);
                return ContentUris.withAppendedId(uri, id);
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
	}

	@Override
	public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		return 0;
	}

	@Override
	public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final Context context = getContext();
        if (context == null) return 0;

        int count;
        switch (MATCHER.match(uri)) {
            case CODE_ARTICLES_DIR:
                count = articlesDao.get().deleteArticles();
                context.getContentResolver().notifyChange(uri, null);
                return count;
            case CODE_ARTICLES_ITEM:
                count = articlesDao.get().deleteArticle(ContentUris.parseId(uri));
                context.getContentResolver().notifyChange(uri, null);
                return count;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
	}



    @Override
    public AndroidInjector<ContentProvider> contentProviderInjector() {
        return dispatchingAndroidInjector;
    }
}
