package org.example.model;

public class WasteItem {
    private int itemId;
    private String itemName;
    private String itemType;
    private Integer parentId;
    private String typeName;


    public WasteItem(int itemId, String itemName, String itemType, Integer parentId) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemType = itemType;
        this.parentId = parentId;
    }
    public WasteItem(int itemId, String itemName, String itemType, Integer parentId, String typeName) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemType = itemType;
        this.typeName = typeName;
        this.parentId = parentId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }
    public String getTypeName() {
        return typeName;
    }
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
    @Override
    public String toString() {
        return itemName;
    }
}
