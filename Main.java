import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

class Contact {
    String name;
    String phone;
    LocalDateTime createdAt;

    Contact(String name, String phone) {
        this.name = name;
        this.phone = phone;
        this.createdAt = LocalDateTime.now();
    }
}

class TrieNode {
    TrieNode[] children = new TrieNode[26];
    boolean isEnd;
}

class Node {
    Contact data;
    Node next;
    Node prev;

    Node(Contact data) {
        this.data = data;
    }
}

class Action {
    String type;
    Contact contact;

    Action(String type, Contact contact) {
        this.type = type;
        this.contact = contact;
    }
}

class Trie {
    TrieNode root = new TrieNode();

    void insert(String word) {
        TrieNode node = root;

        for (char c : word.toLowerCase().toCharArray()) {
            int index = c - 'a';

            if (node.children[index] == null) {
                node.children[index] = new TrieNode();
            }

            node = node.children[index];
        }

        node.isEnd = true;
    }
        List<String> startsWith(String prefix) {
        List<String> result = new ArrayList<>();
        TrieNode node = root;

        for (char c : prefix.toLowerCase().toCharArray()) {
            int index = c - 'a';

            if (node.children[index] == null) {
                return result;
            }

            node = node.children[index];
        }

        dfs(node, prefix, result);
        return result;
    }
        void dfs(TrieNode node, String prefix, List<String> result) {
        if (node.isEnd) {
            result.add(prefix);
        }

        for (int i = 0; i < 26; i++) {
            if (node.children[i] != null) {
                char c = (char) (i + 'a');
                dfs(node.children[i], prefix + c, result);
            }
        }
    }
}



public class Main {

    static DoublyLinkedList contacts = new DoublyLinkedList();

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        contacts.loadFromFile();

        while (true) {
            System.out.println("\n--- PHONE BOOK ---");
            System.out.println("1. Add Contact");
            System.out.println("2. View Contacts");
            System.out.println("3. Search Contact");
            System.out.println("4. Delete Contact");
            System.out.println("5. Exit");
            System.out.println("6. Undo");
            System.out.println("7. Show Recent Searches");
            System.out.println("8. Suggest Contacts");

            int choice = sc.nextInt();
            sc.nextLine();

            String name = "";
            String phone = "";

            if (choice == 1) {
                System.out.print("Enter name: ");
                name = sc.nextLine().trim();
                System.out.print("Enter phone: ");
                phone = sc.nextLine();
            } else if (choice == 3 || choice == 4) {
                System.out.print("Enter name: ");
                name = sc.nextLine();
            }

            switch (choice) {
                case 1:
                    contacts.insert(new Contact(name, phone));
                    break;
                case 2:
                    contacts.display();
                    break;
                case 3:
                    Node result = contacts.search(name);
                    if (result != null) {
                        System.out.println("Found: " + result.data.name + " - " + result.data.phone);
                    } else {
                        System.out.println("Not found");
                    }
                    break;
                case 4:
                    contacts.delete(name);
                    break;
                case 5:
                    contacts.saveToFile();
                    System.out.println("Exiting...");
                    return;
                case 6:
                    contacts.undo();
                    break;
                case 7:
                    contacts.showRecent();
                    break;
                case 8:
                    System.out.print("Enter prefix: ");
                    String prefix = sc.nextLine();
                    contacts.suggest(prefix);
                    break;
                default:
                    System.out.println("Invalid choice");
            }
        }
    }
}

class DoublyLinkedList {

    HashMap<String, Node> map = new HashMap<>();
    Stack<Action> undoStack = new Stack<>();
    Queue<String> recent = new LinkedList<>();
    Trie trie = new Trie();
    Node head;

    // ✅ INSERT
    void insert(Contact contact) {
        Node newNode = new Node(contact);
        trie.insert(contact.name);

        if (head == null) {
            head = newNode;
        } else {
            Node temp = head;
            while (temp.next != null) temp = temp.next;
            temp.next = newNode;
            newNode.prev = temp;
        }

        map.put(contact.name.toLowerCase(), newNode);
        undoStack.push(new Action("add", contact));

        System.out.println("Contact added!");
    }

    // ✅ INSERT WITHOUT UNDO
    void insertWithoutUndo(Contact contact) {
        Node newNode = new Node(contact);
        trie.insert(contact.name);

        if (head == null) {
            head = newNode;
        } else {
            Node temp = head;
            while (temp.next != null) temp = temp.next;
            temp.next = newNode;
            newNode.prev = temp;
        }

        map.put(contact.name.toLowerCase(), newNode);
    }

    // ✅ DELETE
    void delete(String name) {
        Node node = map.get(name.toLowerCase());

        if (node == null) {
            System.out.println("Contact not found");
            return;
        }

        undoStack.push(new Action("delete", node.data));

        deleteWithoutUndo(name);

        System.out.println("Deleted successfully");
    }

    // ✅ DELETE WITHOUT UNDO
    void deleteWithoutUndo(String name) {
        Node node = map.get(name.toLowerCase());
        if (node == null) return;

        if (node.prev != null) node.prev.next = node.next;
        else head = node.next;

        if (node.next != null) node.next.prev = node.prev;

        map.remove(name.toLowerCase());
    }

    // ✅ UNDO
    void undo() {
        if (undoStack.isEmpty()) {
            System.out.println("Nothing to undo");
            return;
        }

        Action last = undoStack.pop();

        if (last.type.equals("add")) {
            deleteWithoutUndo(last.contact.name);
            System.out.println("Undo: Add reversed");
        } else if (last.type.equals("delete")) {
            insertWithoutUndo(last.contact);
            System.out.println("Undo: Delete reversed");
        }
    }

    // ✅ SEARCH + QUEUE
    Node search(String name) {
        Node result = map.get(name.toLowerCase());

        if (result != null) {
            recent.add(name);
            if (recent.size() > 5) recent.poll();
        }

        return result;
    }

    // ✅ RECENT
    void showRecent() {
        if (recent.isEmpty()) {
            System.out.println("No recent searches");
            return;
        }

        System.out.println("Recent Searches:");
        for (String s : recent) System.out.println(s);
    }

    // ✅ DISPLAY
    void display() {
    Node temp = head;

    DateTimeFormatter formatter =
        DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    while (temp != null) {
        System.out.println(
            temp.data.name + " - " +
            temp.data.phone + " - " +
            temp.data.createdAt.format(formatter)
        );
        temp = temp.next;
    }
}

    // ✅ SAVE (WITH TIME)
    void saveToFile() {
        try {
            java.io.BufferedWriter writer =
                    new java.io.BufferedWriter(new java.io.FileWriter("contacts.txt"));

            Node temp = head;
            while (temp != null) {
                writer.write(temp.data.name + "," +
                        temp.data.phone + "," +
                        temp.data.createdAt);
                writer.newLine();
                temp = temp.next;
            }

            writer.close();
            System.out.println("Saved to file");

        } catch (Exception e) {
            System.out.println("Error saving file");
        }
    }

    // ✅ LOAD (WITH TIME)
    void loadFromFile() {
        try {
            java.io.BufferedReader reader =
                    new java.io.BufferedReader(new java.io.FileReader("contacts.txt"));

            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");

                String name = parts[0];
                String phone = parts[1];
                LocalDateTime time = LocalDateTime.now();

                if (parts.length > 2) {
                    time = LocalDateTime.parse(parts[2]);
                }   

                Contact c = new Contact(name, phone);
                c.createdAt = time;

                insertWithoutUndo(c);
            }

            reader.close();
            System.out.println("Data loaded");

        } catch (Exception e) {
            System.out.println("No previous data");
        }
    }
    void suggest(String prefix) {
    List<String> list = trie.startsWith(prefix);

    if (list.isEmpty()) {
        System.out.println("No suggestions");
    } else {
        System.out.println("Suggestions:");
        for (String s : list) {
            System.out.println(s);
        }
    }
}
}