package org.underdev.today.tests;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.test.ProviderTestCase2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This abstract test case deals with common CRUD testing. It inserts, deletes, queries and updates
 * elements in a given content uri.
 *
 * The main idea is that writing test cases for new content types is trivial, just by implementing
 * a few methods.
 *
 * @param <T> The content provider type.
 */
public abstract class CrudProviderTestCase<T extends ContentProvider> extends ProviderTestCase2<T> {
    private Context context;
    private ContentResolver contentResolver;

    /**
     * Builds a {@link org.underdev.today.tests.CrudProviderTestCase} for a provider and authority.
     *
     * @param providerClass The provider class.
     * @param providerAuthority The content authority name.
     */
    public CrudProviderTestCase(Class<T> providerClass, String providerAuthority) {
        super(providerClass, providerAuthority);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        this.context = getMockContext();
        this.contentResolver = getMockContentResolver();
    }

    /**
     * Obtains the default content Uri for the elements that will be tested.
     *
     * @return A Uri.
     */
    protected abstract Uri getContentUri();

    /**
     * Obtains the item Uri with a given ID.
     *
     * //TODO: Should ids be generic types?
     *
     * @param id The item id.
     * @return A Uri pointing to the item id.
     */
    protected abstract Uri getItemUri(String id);

    /**
     * Obtains random content that can be used to fill the Uri described in {@link #getItemUri(String)}.
     *
     * @return Random content values.
     */
    protected abstract ContentValues getRandomContent();

    /**
     * Obtains random update content. Typically this differs from {@link #getRandomContent()} because
     * we don't want IDs to be involved.
     *
     * @return The update contents.
     */
    protected abstract ContentValues getRandomUpdateContent();

    /**
     * Obtains the item Uri for the element described by the values.
     *
     * @param values The values.
     * @return A Uri pointing to the values.
     */
    protected abstract Uri itemUriForValues(ContentValues values);

    /**
     * Tests insertion in the database.
     */
    public void testInsertion() {
        checkEmpty();

        ContentValues values = insertMany(1).get(0);

        checkNotEmpty();

        List<ContentValues> databaseValues = contentAsValues(getContentUri());
        assertEquals(1, databaseValues.size());
        assertValuesMatch(values, databaseValues.get(0));
    }

    private void assertValuesMatch(ContentValues reference, ContentValues candidate) {
        assertEquals(reference.size(), candidate.size());
        assertEquals(reference.keySet(), candidate.keySet());

        for (Map.Entry<String, Object> value : reference.valueSet()) {
            String keyName = value.getKey();
            String valueAsString = reference.getAsString(keyName);

            assertEquals(valueAsString, candidate.getAsString(keyName));
        }
    }

    /**
     * Tests get (an insertion implicitly).
     */
    public void testGet() {
        checkEmpty();

        ArrayList<ContentValues> values = insertMany(5);

        for (ContentValues value : values) {
            Uri uri = itemUriForValues(value);

            List<ContentValues> itemList = contentAsValues(itemUriForValues(value));
            assertEquals(1, itemList.size());
            assertValuesMatch(value, itemList.get(0));
        }
    }

    /**
     * Tests deletions.
     */
    public void testDelete() {
        checkEmpty();

        ArrayList<ContentValues> values = insertMany(5);

        for (ContentValues value : values) {
            List<ContentValues> itemList = contentAsValues(itemUriForValues(value));
            assertEquals(1, itemList.size());
            assertValuesMatch(value, itemList.get(0));
        }
    }

    /**
     * Tests multi-deletions.
     */
    public void testMultiDelete() {
        checkEmpty();

        insertMany(20);

        checkNotEmpty();

        contentResolver.delete(getContentUri(), null, null);

        checkEmpty();
    }

    /**
     * Tests updates.
     */
    public void testUpdate() {
        checkEmpty();

        ArrayList<ContentValues> values = insertMany(5);

        for (ContentValues value : values) {
            Uri itemUri = itemUriForValues(value);
            ContentValues newValues = mergeMissing(value, getRandomUpdateContent());

            this.contentResolver.update(itemUri, newValues, null, null);

            List<ContentValues> updated = contentAsValues(itemUri);
            assertEquals(1, updated.size());
            assertValuesMatch(newValues, updated.get(0));
        }
    }

    // Some pre-defined conditions

    /**
     * Checks whether the content Uri is empty.
     */
    protected void checkEmpty() {
        Cursor cursor = getQueryCursorFromUri(getContentUri());
        assertNotNull(cursor);
        assertEquals("Content is empty", 0, cursor.getCount());
    }

    /**
     * Checks if the database is not empty.
     */
    protected void checkNotEmpty() {
        Cursor cursor = getQueryCursorFromUri(getContentUri());
        assertNotNull(cursor);
        assertNotSame("Database is empty", 0, cursor.getCount());
    }

    /**
     * Obtains a query cursor from a given Uri.
     *
     * @param uri The Uri.
     * @return The cursor.
     */
    private Cursor getQueryCursorFromUri(Uri uri) {
        return this.contentResolver.query(uri, null, null, null, null);
    }

    /**
     * Creates a new instance of {@link android.content.ContentValues} containing
     * the same elements as {@code base} but overlayed with {@code overlay}.
     *
     * @param base The base values.
     * @param overlay The overlay.
     * @return A new instance of {@link android.content.ContentValues} with the above semantics.
     */
    private ContentValues mergeMissing(ContentValues base, ContentValues overlay) {
        ContentValues mergedValues = new ContentValues(base);
        mergedValues.putAll(overlay);
        return mergedValues;
    }

    /**
     * Obtains all the items in a content uri as {@link android.content.ContentValues}.
     *
     * @param uri The uri.
     * @return A list of {@link android.content.ContentValues} contained in the Uri.
     */
    protected List<ContentValues> contentAsValues(Uri uri) {
        Cursor cursor = getQueryCursorFromUri(uri);

        List<ContentValues> values = new ArrayList<ContentValues>(cursor.getCount());
        while (cursor.moveToNext()) {
            ContentValues contentValues = new ContentValues();
            DatabaseUtils.cursorRowToContentValues(cursor, contentValues);
            values.add(contentValues);
        }

        return values;
    }

    /**
     * Inserts a given count of random elements in a content Uri.
     *
     * @param count The number of elements to insert.
     * @return A collections of added {@link android.content.ContentValues}.
     */
    protected ArrayList<ContentValues> insertMany(int count) {
        ArrayList<ContentValues> values = new ArrayList<ContentValues>();
        for (int i = 0; i < count; i++) {
            values.add(insertOne());
        }
        return values;
    }

    /**
     * Inserts a single element to the {@link android.content.ContentProvider}.
     * @return The inserted content.
     */
    protected ContentValues insertOne() {
        ContentValues values = getRandomContent();
        getMockContentResolver().insert(getContentUri(), values);
        return values;
    }
}
