package com.example.qrhunter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RankOwnerProfileList {

    ArrayList<User> list = new ArrayList<>();

    /**
     * This adds a city to the list if the city does not exist
     * @param user
     *      This is a candidate city to add
     */
    public void add(User user) {
        if (list.contains(user)) {
            throw new IllegalArgumentException();
        }
        list.add(user);
    }


    /**
     * sort the userlist from highest to lowest
     * @return
     */
    public List<User> sort() {
        ArrayList<User> list1 = new ArrayList<>();
        Collections.sort(list, new Comparator<User>() {
            @Override
            public int compare(User user, User user2) {
                int i=-1*(user.getTotal()-user2.getTotal());
                return i;
            }
        });
        list1 = list;
        return list1;
    }
    /**
     * find the user in the list
     * @param userId
     * @return
     */

    public int find(String userId) {
        int position = 0;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getUserName().equals(userId)) {
                position = i + 1;
                break;
            }
        }
        return position;
    }
    /**
     * this list has the user
     * @param user
     * @return
     */

    public boolean hasUser(User user){
        if (list.contains(user)) {
            return true;
        }
        else{
            return false;
        }
    }

    /**
     * This Checks if a city is present in the list. If it does then remove it from the list, if not then throw an exception
     * @param user
     *      This is a candidate city to delete
     */
    public void deleteUser(User user){
        if (list.contains(user)) {
            list.remove(user);
        }
        else{
            throw new IllegalArgumentException();
        }
    }
    /**
     * find the user who have the highest score
     * @return
     */

    public String  findHigh(){
        Collections.sort(list, new Comparator<User>() {
            @Override
            public int compare(User user, User user2) {
                int i=-1*(user.getTotal()-user2.getTotal());
                return i;
            }
        });
        return list.get(0).getUserName();
    }
    /**
     * find the user who has the lowest score
     * @return
     */

    public String findLow(){
        Collections.sort(list, new Comparator<User>() {
            @Override
            public int compare(User user, User user2) {
                int i=-1*(user.getTotal()-user2.getTotal());
                return i;
            }
        });
        return list.get(list.size()-1).getUserName();
    }





}
