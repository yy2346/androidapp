import android.provider.BaseColumns

object FeedReaderContract {

    object FeedEntry : BaseColumns {
        const val TABLE_NAME = "favorite_things2"
        const val COLUMN_NAME_SUBTITLE = "thing"
    }

}