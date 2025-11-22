public class sns_network {
    public static void main(String []args) {
        sns network = new sns();
    }

    public List<User> userFollowerSort(List<User> users){ 
        //This method ranks the users by the amount of friends/followers they have
        List<User> allUsers = new ArrayList<>(users.values());
        Sorts.heapSort(allUsers, Comparator.comparingInt(u -> -graph.getNeighbors(u.id).size()));
        return allUsers;
    }
}