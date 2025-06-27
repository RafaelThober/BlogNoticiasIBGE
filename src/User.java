import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class User {
    private String nickname;
    private Map<ListName, Set<Integer>> lists;

    public User(String nickname) {
        this.nickname = nickname;
        this.lists = new EnumMap<>(ListName.class);
        for (ListName list : ListName.values()) {
            lists.put(list, new HashSet<>());
        }
    }

    public String getNickname() {
        return nickname;
    }

    public boolean addToList(ListName list, int newsId) {
        return lists.get(list).add(newsId);
    }

    public boolean removeFromList(ListName list, int newsId) {
        return lists.get(list).remove(newsId);
    }

    public Set<Integer> getList(ListName list) {
        return lists.get(list);
    }

    public boolean isInList(int newsId, ListName listName) {
        return lists.get(listName).contains(newsId);
    }

    public Map<ListName, Set<Integer>> getAllLists() {
        return lists;
    }
}