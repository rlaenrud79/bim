package com.devo.bim.model.entity;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class MySnapShot {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    private int sortNo;
    private String title;
    private String source;

    private String viewPointJson;
    private String viewModelJson;
    private String viewModelId;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="owner_id")
    private Account owner;

    private LocalDateTime writeDate;

    private String searchModelId;

    @OneToMany(fetch = LAZY, mappedBy = "mySnapShot")
    private List<MySnapShotFile> mySnapShotFiles = new ArrayList<>();
        
    public MySnapShot(long id){
        this.id = id;
    }
    
    public MySnapShot(int sortNo, String title, String source, String viewPointJson, String viewModelJson, String viewModelIds, long ownerId) 
    {
    	 this.sortNo = sortNo;
         this.title = title;
         this.source = source;
         this.viewPointJson = viewPointJson;
         this.viewModelJson = viewModelJson;
         this.viewModelId = viewModelIds;
         this.searchModelId = getSearchModelId(viewModelIds);
         this.owner = new Account(ownerId);
         this.writeDate = LocalDateTime.now();
    }
    
    private String getSearchModelId(String viewModelIds){
        String[] viewsModeIdArray = viewModelIds.split(",");

        String searchModelId = "";
        for (String s : viewsModeIdArray) {
            searchModelId += "(" + s + "),";
        }

        return searchModelId.substring( 0, searchModelId.lastIndexOf( "," ) );
    }
    
    public boolean containOneMore(String[] renderedModelIds)
    {
        String[] viewModelIds = viewModelId.split(",");
        for (String viewModelId : viewModelIds){
            for (String renderedModelId : renderedModelIds){
                if(viewModelId.equals(renderedModelId)) return true;
            }
        }
        return false;
    }
    
    public MySnapShot setMySnapShotTitle(String title) {
        this.title = title;
        return this;
    }
}
