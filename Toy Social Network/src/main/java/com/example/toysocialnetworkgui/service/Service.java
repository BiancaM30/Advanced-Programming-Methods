package com.example.toysocialnetworkgui.service;

import com.example.toysocialnetworkgui.domain.*;
import com.example.toysocialnetworkgui.repository.Repository;
import com.example.toysocialnetworkgui.repository.db.PageUserRepository;
import com.example.toysocialnetworkgui.repository.paging.Page;
import com.example.toysocialnetworkgui.repository.paging.Pageable;
import com.example.toysocialnetworkgui.repository.paging.PageableImplementation;
import com.example.toysocialnetworkgui.utils.Graph;
import com.example.toysocialnetworkgui.utils.events.ChangeEvent;
import com.example.toysocialnetworkgui.utils.observer.Observable;
import com.example.toysocialnetworkgui.utils.observer.Observer;
import com.example.toysocialnetworkgui.validators.ServiceException;
import com.example.toysocialnetworkgui.validators.ValidationException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.lang.Math.abs;


public class Service implements Observable<ChangeEvent> {
    //private Repository<Long, User> userRepo;
    private PageUserRepository userRepo;
    private Repository<Tuple<Long, Long>, Friendship> friendshipRepo;
    private Repository<Long, Message> messageRepo;
    private Repository<Tuple<Long, Long>, FriendshipRequest> requestRepo;
    private Repository<Long, Event> eventRepo;
    private Repository<Tuple<Long, Long>, Notification> notificationRepo;

    public Service(PageUserRepository userRepo, Repository<Tuple<Long, Long>, Friendship> friendshipRepo, Repository<Long, Message> messageRepo,
                   Repository<Tuple<Long, Long>, FriendshipRequest> requestRepo, Repository<Long, Event> eventRepo, Repository<Tuple<Long, Long>, Notification> notificationRepo) {
        this.userRepo = userRepo;
        this.friendshipRepo = friendshipRepo;
        this.requestRepo = requestRepo;
        this.messageRepo = messageRepo;
        this.eventRepo = eventRepo;
        this.notificationRepo = notificationRepo;
    }

    /**
     * Add a user to repository
     *
     * @param firstName: user's firstName
     * @param lastName:  users's lastName
     * @param gender:    user's gender
     * @param birthday:  user's birthday
     * @param location:  user's location
     * @param email:     user's email
     * @throws ServiceException    if id already exists
     * @throws ValidationException if user is invalid
     */

    public void addUser(String firstName, String lastName, String gender, LocalDate birthday, String location, String email, String password) {
        User user = new User(firstName, lastName, gender, birthday, location, email, password);
        user.setId(userRepo.nextId());
        userRepo.save(user);
    }


    /**
     * Add a friendship to repository
     *
     * @param id1: id of the first user in the friendship
     * @param id2: id of the second user in the friendship
     * @throws ServiceException    if one of the id's doesn't exist
     * @throws ServiceException    if friendship already exists
     * @throws ValidationException if friendship is invalid
     */
    public void addFriendship(Long id1, Long id2) {
        if (userRepo.findOne(id1) == null || userRepo.findOne(id2) == null) {
            throw new ServiceException("Users don't exist!");
        }
        Tuple<Long, Long> tuple = new Tuple<>(id1, id2);
        if (friendshipRepo.findOne(tuple) != null) {
            throw new ServiceException("Friendship already exists!\n");
        }
        friendshipRepo.save(new Friendship(id1, id2));
        notifyObservers(new ChangeEvent("friendship"));
    }

    /**
     * Removes user and all his friendships from repository
     *
     * @param id: id of the user to be deleted
     * @throws ServiceException if user doesn't exist
     */
    public void removeUser(Long id) {
        if (userRepo.findOne(id) == null) {
            throw new ServiceException("User doesn't exist!\n");
        }
        userRepo.delete(id);
        List<Tuple> toDelete = new ArrayList<>();

        List<Long> removeFriendships = new ArrayList<>();
        for (Friendship fr : friendshipRepo.findAll()) {
            if (fr.getId().getLeftMember().equals(id) || fr.getId().getRightMember().equals(id)) {
                toDelete.add(fr.getId());
            }
        }
        for (Tuple elem : toDelete) {
            friendshipRepo.delete(elem);
        }
    }

    /**
     * Removes friendship from repository
     *
     * @param id1: id of the first user in the friendship
     * @param id2: id of the second user in the friendship
     * @throws ServiceException if friendship doesn't exist
     */
    public void removeFriendship(Long id1, Long id2) {
        Tuple<Long, Long> tuple = new Tuple<>(id1, id2);
        if (friendshipRepo.findOne(tuple) == null) {
            throw new ServiceException("Friendship doesn't exist!\n");
        }
        friendshipRepo.delete(tuple);
        requestRepo.delete(new Tuple<>(id1, id2));
        notifyObservers(new ChangeEvent("friendship"));
        notifyObservers(new ChangeEvent("friendship"));
    }

    public void updateUser(Long id, String firstName, String lastName, String gender, LocalDate birthday, String location, String email, String password) {
        User user = new User(id, firstName, lastName, gender, birthday, location, email, password);
        if (userRepo.findOne(id) == null) {
            throw new ServiceException("User doesn't exist!\n");
        }
        userRepo.update(user);
    }

    public void updateFriendship(Long id1, Long id2, LocalDate date) {
        Friendship friendship = new Friendship(id1, id2, date);
        if (friendshipRepo.findOne(new Tuple<>(id1, id2)) == null) {
            throw new ServiceException("Friendship doesn't exist!\n");
        }
        friendshipRepo.update(friendship);
    }

    /**
     * @return all users from repository
     */
    public Iterable<User> getAllUsers() {
        return userRepo.findAll();
    }

    /**
     * @return all friendships from repository
     */
    public Iterable<Friendship> getAllFriendships() {
        return friendshipRepo.findAll();
    }

    public Iterable<FriendshipRequest> getAllRequests() {
        return requestRepo.findAll();
    }


    /**
     * @param id of the user
     * @return the list of friends of a user with a specified id
     * @throws ServiceException if user doesn't exists
     */
    public List<User> getAllFriends(Long id) {
        if (userRepo.findOne(id) == null) {
            throw new ServiceException("User doesn't exist!\n");
        }

        Iterable<Friendship> friendships = friendshipRepo.findAll();
        List<User> userFriends = new ArrayList<>();
        for (Friendship fr : friendships) {
            if (fr.getId().getLeftMember().equals(id)) {
                userFriends.add(userRepo.findOne(fr.getId().getRightMember()));
            } else if (fr.getId().getRightMember().equals(id)) {
                userFriends.add(userRepo.findOne(fr.getId().getLeftMember()));
            }
        }
        return userFriends;
    }

    public Page<User> getAllFriendsPaginated(int page, Long id_user) {
        Pageable p = new PageableImplementation(page, 3);
        return userRepo.findAllFriends(p, id_user);
    }

    /**
     * @param id of the user
     * @return the list of all friendship requests that the user with a specified id didn't
     * give a response yet
     * @throws ServiceException if user doesn't exists
     */
    public List<FriendshipRequest> getAllUserRequests(Long id) {
        if (userRepo.findOne(id) == null) {
            throw new ServiceException("User doesn't exist!\n");
        }

        List<FriendshipRequest> rez = new ArrayList<>();
        Iterable<FriendshipRequest> requests = requestRepo.findAll();
        for (FriendshipRequest req : requests) {
            if (req.getStatus() == Status.PENDING) {
                if (req.getId().getRightMember().equals(id)) {
                    rez.add(req);
                }
            }
        }
        return rez;
    }


    /**
     * Return all friends of the user with the specified id and the date when they became friends
     *
     * @param id
     * @return a map containing users as key and a LocalDate as value
     */
    public Map<User, LocalDate> getAllFriendsStream(Long id) {
        if (userRepo.findOne(id) == null) {
            throw new ServiceException("User doesn't exist!\n");
        }

        Iterable<Friendship> itrFriendships = friendshipRepo.findAll();
        List<Friendship> friendships = new ArrayList<>();
        itrFriendships.forEach(friendships::add);

        Map<User, LocalDate> friendsMap = new HashMap<>();

        Predicate<Friendship> pLeft = fr -> fr.getId().getLeftMember().equals(id);
        Predicate<Friendship> pRight = fr -> fr.getId().getRightMember().equals(id);

        friendships.stream()
                .filter(pLeft.or(pRight))
                .forEach(x -> {
                    Long idOfFriend = (x.getId().getLeftMember() == id ? x.getId().getRightMember() : x.getId().getLeftMember());
                    friendsMap.put(userRepo.findOne(idOfFriend), x.getDate());
                });

        return friendsMap;
    }

    public int getNumberOfCommunities() {

        Graph g = new Graph(userRepo.getNumberOfEntites() + 1);
        for (User user : userRepo.findAll()) {
            List<User> allFriendsOfUser = this.getAllFriends(user.getId());
            for (User friend : allFriendsOfUser) {
                g.addEdge(user.getId(), friend.getId());
            }
        }

        return g.connectedComponents();

    }

    public int getLargestCommunity() {

        Graph g = new Graph(userRepo.getNumberOfEntites() + 1);
        for (User user : userRepo.findAll()) {
            List<User> allFriendsOfUser = this.getAllFriends(user.getId());
            for (User friend : allFriendsOfUser) {
                g.addEdge(user.getId(), friend.getId());
            }
        }

        return g.largestConnectedComponent();

    }

    public Map<User, LocalDate> getPrietenie(int luna, Long idUser) {
        Iterable<Friendship> itrPrietenii = friendshipRepo.findAll();
        List<Friendship> prietenii = new ArrayList<>();
        itrPrietenii.forEach(prietenii::add);

        Map<User, LocalDate> mapRez = new HashMap<>();

        prietenii.stream()
                .filter(x -> x.getId().getLeftMember().equals(idUser))
                .filter(x -> x.getDate().getMonthValue() == luna)
                .forEach(x -> {
                    mapRez.put(userRepo.findOne(x.getId().getRightMember()), x.getDate());
                });

        prietenii.stream()
                .filter(x -> x.getId().getRightMember().equals(idUser))
                .filter(x -> x.getDate().getMonthValue() == luna)
                .forEach(x -> {
                    mapRez.put(userRepo.findOne(x.getId().getLeftMember()), x.getDate());
                });
        return mapRez;
    }

    public void sendRequest(Long id1, Long id2) {

        if (userRepo.findOne(id1) == null || userRepo.findOne(id2) == null) {
            throw new ServiceException("Users don't exist!");
        }
        Tuple<Long, Long> tuple = new Tuple<>(id1, id2);
        if (requestRepo.findOne(tuple) != null) {
            FriendshipRequest request = requestRepo.findOne(tuple);
            if (request.getStatus() == Status.PENDING)
                throw new ServiceException("Request already sent!");
            if (request.getStatus() == Status.APPROVED)
                throw new ServiceException("Request already approved!");
            if (request.getStatus() == Status.REJECTED) {
                FriendshipRequest req = new FriendshipRequest(id1, id2);
                req.setSendingDate(LocalDate.now());
                requestRepo.update(req);
            }
        }

        FriendshipRequest request = new FriendshipRequest(id1, id2);
        request.setSendingDate(LocalDate.now());
        requestRepo.save(request);

        notifyObservers(new ChangeEvent("request"));
    }

    public void unsendRequest(Long id1, Long id2) {

        if (requestRepo.findOne(new Tuple<>(id1, id2)) == null) {
            throw new ServiceException("Request doesn't exist!");
        }

        if (requestRepo.findOne(new Tuple<>(id1, id2)).getStatus() != Status.PENDING) {
            throw new ServiceException("Request already has an answer!");
        }

        if (userRepo.findOne(id1) == null || userRepo.findOne(id2) == null) {
            throw new ServiceException("Users don't exist!");
        }


        FriendshipRequest request = new FriendshipRequest(id1, id2);

        requestRepo.delete(new Tuple<Long, Long>(id1, id2));
        notifyObservers(new ChangeEvent("request"));
    }


    public void acceptRequest(Long id1, Long id2) {

        if (requestRepo.findOne(new Tuple<>(id1, id2)) == null) {
            throw new ServiceException("Request doesn't exist!");
        } else if (requestRepo.findOne(new Tuple<>(id1, id2)).getStatus() == Status.PENDING) {
            FriendshipRequest newReq = new FriendshipRequest(id1, id2);
            newReq.setStatus(Status.APPROVED);
            requestRepo.update(newReq);
            friendshipRepo.save(new Friendship(id1, id2));

            notifyObservers(new ChangeEvent("request"));
            notifyObservers(new ChangeEvent("friendship"));


        } else
            throw new ServiceException("Request is already " + requestRepo.findOne(new Tuple<>(id1, id2)).getStatus());

    }

    public void rejectRequest(Long id1, Long id2) {

        if (requestRepo.findOne(new Tuple<>(id1, id2)) == null) {
            throw new ServiceException("Request doesn't exist!");
        } else if (requestRepo.findOne(new Tuple<>(id1, id2)).getStatus() == Status.PENDING) {
            FriendshipRequest newReq = new FriendshipRequest(id1, id2);
            newReq.setStatus(Status.REJECTED);
            requestRepo.update(newReq);

            notifyObservers(new ChangeEvent("request"));


        } else
            throw new ServiceException("Request is already " + requestRepo.findOne(new Tuple<>(id1, id2)).getStatus());

    }

    public String getTwoUsersRequestStatus(Long id1, Long id2) {
        String status = "";
        if (requestRepo.findOne(new Tuple<>(id1, id2)) == null) {
            status = "no request";
        } else {
            FriendshipRequest request = requestRepo.findOne(new Tuple<>(id1, id2));
            if (request.getStatus() == Status.APPROVED) status = "friends";
            else if (request.getStatus() == Status.REJECTED) status = "rejected";
            else if (request.getStatus() == Status.PENDING) status = "pending";
        }
        return status;
    }

    public void sendMessage(Long from, List<Long> rezTo, String message) {
        if (userRepo.findOne(from) == null) {
            throw new ServiceException("User " + from.toString() + " doesn't exist!\n");
        }
        rezTo.stream()
                .forEach(x -> {
                    if (userRepo.findOne(x) == null) {
                        throw new ServiceException("User " + x.toString() + " doesn't exist!\n");
                    }
                });
        Message mess = new Message(from, rezTo, message);
        mess.setId(messageRepo.nextId());
        messageRepo.save(mess);
        notifyObservers(new ChangeEvent("mesaj"));
    }

    public void sendReply(Long from, Long idMess, String message) {
        if (userRepo.findOne(from) == null) {
            throw new ServiceException("User with id " + from.toString() + " doesn't exist!\n");
        }
        if (messageRepo.findOne(idMess) == null) {
            throw new ServiceException("Message with id " + idMess.toString() + " doesn't exist!\n");
        }
        Message mess = messageRepo.findOne(idMess);
        //List<Long> recipientList = mess.getTo();

        /*if (recipientList.contains(from) == false) {
            throw new ServiceException("User is not in the recipient list, so it can't reply!");
        }*/

        Message updatedReplyField = new Message(from, Collections.singletonList(mess.getFrom()), message);
        updatedReplyField.setIdReply(mess.getId());
        updatedReplyField.setId(messageRepo.nextId());
        messageRepo.save(updatedReplyField);
    }

    public Message findIdReply(Long id) {
        return messageRepo.findOne(id);
    }

    public List<Message> displayConversation(Long id1, Long id2) {
        if (userRepo.findOne(id1) == null) {
            throw new ServiceException("User with id " + id1.toString() + " doesn't exist!\n");
        }
        if (userRepo.findOne(id2) == null) {
            throw new ServiceException("User with id " + id2.toString() + " doesn't exist!\n");
        }
        Iterable<Message> itrList = messageRepo.findAll();

        List<Message> messages = new ArrayList<>();
        itrList.forEach(messages::add);

        Predicate<Message> p1 = x -> (x.getFrom().equals(id1) && x.getTo().size() == 1 && x.getTo().get(0).equals(id2));
        Predicate<Message> p2 = x -> (x.getFrom().equals(id2) && x.getTo().size() == 1 && x.getTo().get(0).equals(id1));
        List<Message> list = messages.stream()
                .filter(p1.or(p2))
                .collect(Collectors.toList());

        list.sort(new Comparator<>() {
            @Override
            public int compare(Message o1, Message o2) {
                return o1.getData().compareTo(o2.getData());
            }
        });

        return list;
    }

    /**
     * return user if exists
     * else return null
     */
    public User verifyCredentials(String email, String password) {
        Iterable<User> itrUsers = userRepo.findAll();
        List<User> users = new ArrayList<>();
        itrUsers.forEach(users::add);

        for (User user : users) {
            if (user.getEmail().equals(email) && user.getPassword().equals(password))
                return user;
        }
        return null;
    }

    public User getUser(Long id) {
        return userRepo.findOne(id);
    }


    private List<Observer<ChangeEvent>> observers = new ArrayList<>();

    @Override
    public void addObserver(Observer<ChangeEvent> e) {
        observers.add(e);

    }

    @Override
    public void removeObserver(Observer<ChangeEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(ChangeEvent t) {
        observers.stream()
                .forEach(x -> x.update(t));
    }


    public Long searchMessage(Long id, List<Long> recipientList) {
        Iterable<Message> itrList = messageRepo.findAll();
        List<Message> messages = new ArrayList<>();
        itrList.forEach(messages::add);

        if (messages.size() != 0) {
            for (int i = 0; i < messages.size(); i++) {
                if (messages.get(i).getFrom() == id || messages.get(i).getTo().containsAll(recipientList))//&& messages.get(i).getTo().containsAll(recipientList))
                {

                    return messages.get(i).getId();
                }
            }
        }
        return null;
    }

    public Long searchLastMessage(Long id, List<Long> recipientList) {
        Iterable<Message> itrList = messageRepo.findAll();
        List<Message> messages = new ArrayList<>();
        itrList.forEach(messages::add);

        // List<Message> messageListRez = new ArrayList<>();
        if (messages.size() != 0) {
            for (int i = 0; i < messages.size(); i++) {
                if (messages.get(i).getFrom() == id || messages.get(i).getTo().equals(id)) //&& messages.get(i).getTo().containsAll(recipientList))
                {
                    return messages.get(i).getId();
                }
            }
        }
        return null;
    }

    public Long searchLastMessage2(List<Long> recipientList, Long id) {
        Iterable<Message> itrList = messageRepo.findAll();
        List<Message> messages = new ArrayList<>();
        itrList.forEach(messages::add);

        // List<Message> messageListRez = new ArrayList<>();

        if (messages.size() != 0) {
            for (int i = messages.size() - 1; i >= 0; i--) {
                if (messages.get(i).getFrom() == id || messages.get(i).getTo().equals(id)) //&& messages.get(i).getTo().containsAll(recipientList))
                {

                    return messages.get(i).getId();
                }
            }
        }
        return null;
    }

    public void removeConversation() {
        messageRepo.clear();
    }

    public Long getUserName(String name, String lastName) {
        Iterable<User> itrUsers = userRepo.findAll();
        List<User> users = new ArrayList<>();
        itrUsers.forEach(users::add);

        for (User user : users) {
            if (user.getFirstName().equals(name) && user.getLastName().equals(lastName)) {
                return user.getId();
            }


        }
        return null;
    }

    public List<Message> displayGroupConversation(Long idLogged, List<Long> users) {
        for (Long id : users) {
            if (userRepo.findOne(id) == null) {
                throw new ServiceException("User with id " + id.toString() + " doesn't exist!\n");

            }
        }

        Iterable<Message> itrList = messageRepo.findAll();
        List<Message> messages = new ArrayList<>();
        itrList.forEach(messages::add);
        users.add(idLogged);

        List<Message> messageListRez = new ArrayList<>();
        for (Message mesaj : messages) {
            List<Long> userList = new ArrayList<>();
            userList.add(mesaj.getFrom());
            mesaj.getTo().forEach(userList::add);
            if (userList.containsAll(users))
                messageListRez.add(mesaj);
        }

        messageListRez.sort(new Comparator<>() {
            @Override
            public int compare(Message o1, Message o2) {
                return o1.getData().compareTo(o2.getData());
            }
        });

        return messageListRez;

    }

    public List<Message> getAllMessages() {
        Iterable<Message> itrList = messageRepo.findAll();
        List<Message> messages = new ArrayList<>();
        itrList.forEach(messages::add);

        return messages;
    }

    public void addEvent(User organizer, String name, String description, String location, LocalDate date) {
        Event event = new Event(organizer, name, description, location, date);
        event.setId(eventRepo.nextId());
        eventRepo.save(event);
        notificationRepo.save(new Notification(organizer.getId(), event.getId(), LocalDate.now()));

        notifyObservers(new ChangeEvent("event"));
    }

    public List<Event> getAllEvents() {
        Iterable<Event> itrEvents = eventRepo.findAll();
        List<Event> events = new ArrayList<>();
        itrEvents.forEach(events::add);

        return events;
    }

    public User getUserByName(String name) {
        for (User us : userRepo.findAll())
            if (us.getFirstName().equals(name))
                return us;
        return null;
    }


    public void subscribeNotifications(Long id1, Long id2) {

        Tuple<Long, Long> tuple = new Tuple<>(id1, id2);
        if (notificationRepo.findOne(tuple) != null) {
            throw new ServiceException("You are already subscribed!!\n");
        }
        notificationRepo.save(new Notification(id1, id2, LocalDate.now()));
        notifyObservers(new ChangeEvent("notification"));
    }

    public void unsubscribeNotifications(Long id1, Long id2) {
        Tuple<Long, Long> tuple = new Tuple<>(id1, id2);
        if (notificationRepo.findOne(tuple) == null) {
            throw new ServiceException("You are not subscribed to this event!\n");
        }
        notificationRepo.delete(tuple);
        notificationRepo.delete(new Tuple<>(id1, id2));
        notifyObservers(new ChangeEvent("notification"));
    }


    public List<Event> getAllSubscribedEvents(Long id_user) {
        Iterable<Notification> itrNotifications = notificationRepo.findAll();

        List<Event> events = new ArrayList<>();
        for (Notification n : itrNotifications) {
            if (n.getId().getLeftMember() == id_user) {

                events.add(eventRepo.findOne(n.getId().getRightMember()));
            }
        }
        return events;
    }


    public List<Event> getAllNextEvents(Long id_user) {
        Iterable<Notification> itrNotifications = notificationRepo.findAll();

        List<Event> events = new ArrayList<>();
        for (Notification n : itrNotifications) {

            if (n.getId().getLeftMember() == id_user) {
                Event event = eventRepo.findOne(n.getId().getRightMember());
                Long daysBetween = ChronoUnit.DAYS.between(event.getDate(), LocalDate.now());

                if ((event.getDate().isAfter(LocalDate.now())) && (abs(daysBetween) < 10)) {
                    events.add(event);
                }
            }
        }
        return events;
    }
    /*get all friendships of a user with a specified id between two dates*/

    public List<User> getFriendsBetweenTwoDates(Long id, LocalDate start, LocalDate end) {
        List<User> friends = new ArrayList<>();
        for (User fr : getAllFriends(id)) {
            Friendship friendship = friendshipRepo.findOne(new Tuple<>(id, fr.getId()));
            if ((friendship.getDate().isAfter(start) || friendship.getDate().isEqual(start)) && (friendship.getDate().isBefore(end) || friendship.getDate().isEqual(end))) {
                friends.add(fr);
            }

        }
        return friends;
    }

    public List<Message> getMessageBetweenTwoDates(Long id, LocalDate start, LocalDate end) {
        List<Message> msg = new ArrayList<>();

        for (Message mess : messageRepo.findAll()) {
            List<Long> listTo = mess.getTo();
            for (Long var : listTo) {
                if (var == id)
                    if (mess.getTo().contains(id) && (mess.getData().toLocalDate().isAfter(start)
                            || mess.getData().toLocalDate().isEqual(start)) &&
                            (mess.getData().toLocalDate().isBefore(end) || mess.getData().toLocalDate().isEqual(end))) {
                        msg.add(mess);
                    }
            }
        }
        return msg;
    }

    public List<Message> getMessageFromUserBetweenTwoDates(Long id, Long user, LocalDate start, LocalDate end) {
        List<Message> msg = new ArrayList<>();
        for (Message mess : messageRepo.findAll()) {
            if (mess.getFrom() == user &&
                    mess.getTo().contains(id) &&
                    (mess.getData().toLocalDate().isAfter(start) || mess.getData().toLocalDate().isEqual(start)) &&
                    (mess.getData().toLocalDate().isBefore(end) || mess.getData().toLocalDate().isEqual(end))
            )
                msg.add(mess);
        }
        return msg;
    }

    /*userii cu care am o conversatie activa*/
    public List<User> getUserConversations(Long id) {
        List<User> users = new ArrayList<>();
        for (Message mess : messageRepo.findAll()) {
            if (mess.getTo().size() == 1 && (mess.getTo().get(0).equals(id) || mess.getFrom() == id))
                if (mess.getFrom() == id)
                    users.add(userRepo.findOne(mess.getTo().get(0)));
                else {
                    users.add(userRepo.findOne(mess.getFrom()));
                }

        }
        List<User> rez = new ArrayList<User>(new HashSet<User>(users));
        return rez;
    }

    public List<String> getUsersFromGroupConversations(Long id) {
        List<String> rez = new ArrayList<>();


        for (Message mess : messageRepo.findAll()) {
            if (mess.getTo().size() > 1 && (mess.getTo().contains(id) || mess.getFrom() == id)) {
                if (mess.getFrom() == id)
                //users.add(userRepo.findOne(mess.getTo()));
                {
                    String text = "";
                    for (Long elem : mess.getTo())
                        text = text + userRepo.findOne(elem).getFirstName() + ";";
                    rez.add(text);
                } else {
                    String text = userRepo.findOne(mess.getFrom()).getFirstName() + ";";
                    for (Long elem : mess.getTo())
                        if (elem != id)
                            text = text + userRepo.findOne(elem).getFirstName() + ";";
                    rez.add(text);

                }
            }

        }
        List<String> rezUnique = new ArrayList<String>(new HashSet<String>(rez));
        System.out.println(rezUnique.size());
        for (int i = 0; i < rezUnique.size()-1; i++) {
            char[] first = rezUnique.get(i).toCharArray();
            Arrays.sort(first);
            for (int j = i + 1; j < rezUnique.size(); j++)
            {

                char[] second = rezUnique.get(j).toCharArray();
                Arrays.sort(second);
                if(Arrays.equals(first, second)) {
                    rezUnique.remove(j);
                }
            }

        }

        return rezUnique;
    }


}

