import java.util.*;

class Contact {
    String name;
    String phone;

    Contact(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }
}
class Node {
    Contact data;
    Node next;
    Node prev;

    Node(Contact data) {
        this.data = data;
        this.next = null;
        this.prev = null;
    }
}

class Action {
    String type; // "add", "delete"
    Contact contact;

    Action(String type, Contact contact) {
        this.type = type;
        this.contact = contact;
    }
}

public class Main {

    static DoublyLinkedList contacts = new DoublyLinkedList();

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n--- PHONE BOOK ---");
            System.out.println("1. Add Contact");
            System.out.println("2. View Contacts");
            System.out.println("3. Search Contact");
            System.out.println("4. Delete Contact");
            System.out.println("5. Exit");
            System.out.println("6. Undo");
            System.out.println("7. Show Recent Searches");

            int choice = sc.nextInt();
            sc.nextLine(); // consume newline
            String name = " ";
            String phone = " ";
            if (choice == 1) {
                System.out.print("Enter name: ");
                name = sc.nextLine().trim();
                System.out.print("Enter phone: ");
                phone = sc.nextLine();
            } else if (choice == 3) {
                System.out.print("Enter name to search: ");
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
                    System.out.print("Enter name to delete: ");
                    name = sc.nextLine();
                    contacts.delete(name);
                    break;
                case 5:
                    System.out.println("Exiting...");
                    return;
                case 6:
                    contacts.undo();
                    break;
                case 7:
                    contacts.showRecent();
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
    Node head;

    void insert(Contact contact) {
        Node newNode = new Node(contact);

        if (head == null) {
            head = newNode;
            map.put(contact.name.toLowerCase(), newNode);
            undoStack.push(new Action("add",newNode.data));
            return;
            
        }

        Node temp = head;
        while (temp.next != null) {
            temp = temp.next;
           

    }   temp.next = newNode;
        newNode.prev = temp;
        temp=newNode;
        map.put(contact.name.toLowerCase(), newNode);
        undoStack.push(new Action("add",temp.data));
} 
    void insertWithoutUndo(Contact contact) {
    Node newNode = new Node(contact);

    if (head == null) {
        head = newNode;
    } else {
        Node temp = head;
        while (temp.next != null) {
            temp = temp.next;
        }

        temp.next = newNode;
        newNode.prev = temp;
    }

    map.put(contact.name.toLowerCase(), newNode);
}
    void delete(String name) {
    Node node = map.get(name.toLowerCase());

    if (node == null) {
        System.out.println("Contact not found");
        return;
    }
    undoStack.push(new Action("delete", node.data));

    if (node.prev != null) {
        node.prev.next = node.next;
    } else {
        head = node.next;
    }

    if (node.next != null) {
        node.next.prev = node.prev;
    }

    map.remove(name.toLowerCase());

    System.out.println("Deleted successfully");
} 
    void deleteWithoutUndo(String name) {
    Node node = map.get(name.toLowerCase());

    if (node == null) return;

    if (node.prev != null) {
        node.prev.next = node.next;
    } else {
        head = node.next;
    }

    if (node.next != null) {
        node.next.prev = node.prev;
    }

    map.remove(name.toLowerCase());
}
        void display() {
        Node temp = head;

        while (temp != null) {
            System.out.println(temp.data.name + " - " + temp.data.phone);
            temp = temp.next;
        }
    }

    void undo() {
    if (undoStack.isEmpty()) {
        System.out.println("Nothing to undo");
        return;
    }

    Action last = undoStack.pop();

    if (last.type.equals("add")) {
        deleteWithoutUndo(last.contact.name);
        System.out.println("Undo: Last add reversed");
    }
    else if (last.type.equals("delete")) {
        insertWithoutUndo(last.contact);
        System.out.println("Undo: Deleted contact restored");
    }
}
    Node search(String name) {
            Node result = map.get(name.toLowerCase());

            if (result != null) {
        //  Add to queue
            recent.add(name);

        // limit size to 5
            if (recent.size() > 5) {
            recent.poll(); // removes oldest
        }
    }

    return result;
}
void showRecent() {
    if (recent.isEmpty()) {
        System.out.println("No recent searches");
        return;
    }

    System.out.println("Recent Searches:");
    for (String name : recent) {
        System.out.println(name);
    }
}
}