package top.preacer.database.pojo;

import java.io.File;
import java.io.Serializable;
import java.util.*;

public class IndexTree implements Serializable {
	
	
	public static class IndexNode implements Serializable{
		private static final long serialVersionUID = 1L;
		private List<Index> indexList;

	    public IndexNode() {
	        this.indexList = new ArrayList<>();
	    }

	    public void addIndex(Index index) {
	        indexList.add(index);
	    }

	    public Iterator<Index> indexIterator() {
	        return indexList.iterator();
	    }

	    public Set<File> getFiles() {
	        Set<File> fileSet = new HashSet<>();
	        Iterator<Index> indexIterator = indexIterator();
	        for (Index index : indexList) {
	            File file = new File(index.getFilePath());
	            fileSet.add(file);
	        }
	        return fileSet;
	    }
	}
	public static class IndexKey implements Comparable, Serializable {
		private static final long serialVersionUID = 1L;
		private String value;
	    private String type;

	    public IndexKey(String value, String type) {
	        this.value = value;
	        this.type = type;
	    }


	    @Override
	    public int compareTo(Object ohterValue) {
	        String keyValue = ((IndexKey) ohterValue).getValue();
	        try {
	            switch (type) {
	                case "int":
	                    return Integer.valueOf(value).compareTo(Integer.valueOf(keyValue));
	                case "double":
	                    return Double.valueOf(value).compareTo(Double.valueOf(keyValue));
	                case "varchar":
	                    return value.compareTo(String.valueOf(keyValue));
	                default:
	                    throw new Exception("条件限定不匹配");
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return 0;
	    }


	    @Override
	    public boolean equals(Object o) {
	        if (this == o) {
	            return true;
	        }
	        if (o == null || getClass() != o.getClass()) {
	            return false;
	        }

	        IndexKey indexKey = (IndexKey) o;
	        return value != null ? value.equals(indexKey.value) : indexKey.value == null;
	    }

	    @Override
	    public int hashCode() {
	        return value != null ? value.hashCode() : 0;
	    }

	    public String getValue() {
	        return value;
	    }

	    public String getType() {
	        return type;
	    }
	}
	private static final long serialVersionUID = 1L;
	private TreeMap<IndexKey, IndexNode> treeMap;

    public IndexTree() {
        treeMap = new TreeMap<>();
    }

    public TreeMap<IndexKey, IndexNode> getTreeMap() {
        return treeMap;
    }

    public void setTreeMap(TreeMap<IndexKey, IndexNode> treeMap) {
        this.treeMap = treeMap;
    }

    public List<IndexNode> find(Relate relationship, IndexKey condition) {
        List<IndexNode> indexNodeList = new ArrayList<>();
        Map<IndexKey, IndexNode> indexNodeMap = null;
        switch (relationship) {
            case LESS_THAN:
                //此方法获得小于key的映射
                indexNodeMap = treeMap.headMap(condition);
                if (null != indexNodeMap) {
                    for (IndexNode node : indexNodeMap.values()) {
                        indexNodeList.add(node);
                    }
                }
                /*for (IndexNode node : indexNodeMap.values()) {
                    indexNodeList.add(node);
                }*/
                break;
            case EQUAL_TO:
                IndexNode indexNode = treeMap.get(condition);
                if (null != indexNode) {
                    indexNodeList.add(indexNode);
                }
                break;
            case GREATER_THAN:
                //此方法获得大于等于key的映射，如果有等于那么要去掉等于key的映射
                indexNodeMap = treeMap.tailMap(condition);
                if (null != indexNodeMap) {
                    if (indexNodeMap.containsKey(condition)) {
                        indexNodeMap.remove(condition);
                    }
                    for (IndexNode node : indexNodeMap.values()) {
                        indexNodeList.add(node);
                    }
                }
                break;
            default:
                try {
                    throw new Exception("条件限定不匹配");
                } catch (Exception e) {
                    e.printStackTrace();
                }

        }
        return indexNodeList;
    }

    public Set<File> getFiles(Relate relationship, IndexKey condition) {
        Set<File> fileSet = new HashSet<>();
        List<IndexNode> indexNodes = this.find(relationship, condition);
        for (IndexNode indexNode : indexNodes) {
            fileSet.addAll(indexNode.getFiles());
        }
        return fileSet;
    }

    public void put(IndexKey indexKey, IndexNode indexNode) {
        treeMap.put(indexKey, indexNode);
    }

    public void putIndex(IndexKey indexKey, String filePath, int lineNum) {
        IndexNode indexNode = treeMap.get(indexKey);
        //如果没有此节点，添加此节点
        if (null == indexNode) {
            treeMap.put(indexKey, new IndexNode());
            //将indexNode从新引用到此节点
            indexNode = treeMap.get(indexKey);
        }
        Index index = new Index(filePath, lineNum);
        indexNode.addIndex(index);
    }

    
}
