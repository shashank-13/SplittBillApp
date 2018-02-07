package com.shashank.singh.splitbill.Model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by shashank on 5/2/2017.
 */
@Table(name="Groupmembers")
public class Groupmembers extends Model {

    public Groupmembers() {
    }
    @Column(name="groupName", index = true ,unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public String groupName;

    @Column(name="members", index = true)
    public String members;
}
