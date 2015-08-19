package com.george.xblog.utils;

import java.util.Hashtable;
import java.util.Map;

/**
 * Created by george.yang on 15/8/15.
 */
public class LruCache {

    private static final class Entry {
        public String key;// 键
        public Object value;// 值
        public Entry prev;// 前一节点
        public Entry next;// 后一节点
        public long validTime;//有效的最大时间
    }

    private static LruCache instance;
    public static synchronized LruCache getInstance() {
        if (instance==null) {
            instance = new LruCache(100);
        }
        return instance;
    }





    private int cacheSize;
    private Map<String, Entry> nodes;// 缓存容器
    private Entry first;// 链表头
    private Entry last;// 链表尾


    public LruCache(int i) {
        cacheSize = i;
        nodes = new Hashtable<String, Entry>(i);//缓存容器
    }


    /**
     * 获取缓存中对象,并把它放在最前面
     */
    public <T> T get(String key) {
        Entry node = nodes.get(key);
        if (node != null) {
            if (System.currentTimeMillis()>node.validTime) {
            	//在链表中删除
                remove(node);
                //在hashtable中删除
                nodes.remove(key);
            } else {
                moveToHead(node);
                return (T)node.value;
            }
        }
        return null;
    }

    /**
     * 加入缓存
     * 添加 entry到hashtable, 并把entry
     * @param key
     * @param timeout 超时时间，毫秒
     * @param value
     */
    public void put(String key,long timeout, Object value) {
        //先查看hashtable是否存在该entry, 如果存在，则只更新其value
        Entry node = nodes.get(key);

        if (node == null) {
            //缓存容器是否已经超过大小.
            if (nodes.size() >= cacheSize) {
                nodes.remove(last.key);
                removeLast();
            }
            node = new Entry();
        }
        node.validTime = System.currentTimeMillis() + timeout;
        node.value = value;
        //将最新使用的节点放到链表头，表示最新使用的.
        moveToHead(node);
        nodes.put(key, node);
    }


    /**
     * 将entry删除, 注意：删除操作只有在cache满了才会被执行
     */
    public void remove(String key) {
        Entry node = nodes.get(key);
        //在链表中删除
        remove(node);
        //在hashtable中删除
        nodes.remove(key);
    }


    private void remove(Entry node) {
        if (node != null) {
            if (node.prev != null) {
                node.prev.next = node.next;
            }
            if (node.next != null) {
                node.next.prev = node.prev;
            }
            if (last == node)
                last = node.prev;
            if (first == node)
                first = node.next;
        }
    }

    /**
     * 删除链表尾部节点，即使用最后 使用的entry
     */
    private void removeLast() {
        //链表尾不为空,则将链表尾指向null. 删除连表尾（删除最少使用的缓存对象）
        if (last != null) {
            if (last.prev != null)
                last.prev.next = null;
            else
                first = null;
            last = last.prev;
        }
    }

    /**
     * 移动到链表头，表示这个节点是最新使用过的
     */
    private void moveToHead(Entry node) {
        if (node == first)
            return;
        if (node.prev != null)
            node.prev.next = node.next;
        if (node.next != null)
            node.next.prev = node.prev;
        if (last == node)
            last = node.prev;
        if (first != null) {
            node.next = first;
            first.prev = node;
        }
        first = node;
        node.prev = null;
        if (last == null)
            last = first;
    }

    /*
     * 清空缓存
     */
    public void clear() {
        first = null;
        last = null;
        nodes = new Hashtable<String, Entry>(cacheSize);
    }
}
