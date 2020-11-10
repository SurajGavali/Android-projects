package com.letssolvetogether.omr.omrkey.db;

import android.database.Cursor;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;

@SuppressWarnings({"unchecked", "deprecation"})
public final class OMRKeyDao_Impl implements OMRKeyDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<OMRKey> __insertionAdapterOfOMRKey;

  public OMRKeyDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfOMRKey = new EntityInsertionAdapter<OMRKey>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR REPLACE INTO `OMRKey` (`omrkeyid`,`correct_answers`) VALUES (?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, OMRKey value) {
        stmt.bindLong(1, value.getOmrkeyid());
        if (value.getStrCorrectAnswers() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getStrCorrectAnswers());
        }
      }
    };
  }

  @Override
  public void insertOMRKey(final OMRKey omrKey) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfOMRKey.insert(omrKey);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public OMRKey findById(final int omrkeyid) {
    final String _sql = "SELECT * FROM omrkey WHERE omrkeyid = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, omrkeyid);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfOmrkeyid = CursorUtil.getColumnIndexOrThrow(_cursor, "omrkeyid");
      final int _cursorIndexOfStrCorrectAnswers = CursorUtil.getColumnIndexOrThrow(_cursor, "correct_answers");
      final OMRKey _result;
      if(_cursor.moveToFirst()) {
        _result = new OMRKey();
        final int _tmpOmrkeyid;
        _tmpOmrkeyid = _cursor.getInt(_cursorIndexOfOmrkeyid);
        _result.setOmrkeyid(_tmpOmrkeyid);
        final String _tmpStrCorrectAnswers;
        _tmpStrCorrectAnswers = _cursor.getString(_cursorIndexOfStrCorrectAnswers);
        _result.setStrCorrectAnswers(_tmpStrCorrectAnswers);
      } else {
        _result = null;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }
}
