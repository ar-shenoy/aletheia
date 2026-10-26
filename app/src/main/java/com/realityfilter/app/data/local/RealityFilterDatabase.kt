package com.realityfilter.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.realityfilter.app.data.local.entities.AnalysisSessionEntity
import com.realityfilter.app.data.local.entities.ConversationSessionEntity
import com.realityfilter.app.data.local.entities.Converters
import com.realityfilter.app.data.local.entities.ObservationEntity
import com.realityfilter.app.data.local.entities.PatternEntity

@Database(
    entities = [
        PatternEntity::class,
        ObservationEntity::class,
        AnalysisSessionEntity::class,
        ConversationSessionEntity::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class RealityFilterDatabase : RoomDatabase() {

    abstract fun patternDao(): PatternDao
    abstract fun observationDao(): ObservationDao
    abstract fun analysisSessionDao(): AnalysisSessionDao
    abstract fun conversationSessionDao(): ConversationSessionDao

    companion object {
        @Volatile
        private var INSTANCE: RealityFilterDatabase? = null

        fun getInstance(context: Context): RealityFilterDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RealityFilterDatabase::class.java,
                    "aletheia.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
