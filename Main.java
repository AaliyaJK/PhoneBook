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

public class Main {

    static DoublyLinkedList contacts = new DoublyLinkedList();

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n--- PHONE BOOK ---");
            System.out.println("1. Add Contact");
            System.out.println("2. View Contacts");
            System.out.println("3. Search Contact");
            System.out.println("4. Exit");

            int choice = sc.nextInt();
            sc.nextLine(); // consume newline
            String name = " ";
            String phone = " ";
            if(choice ==1 || choice==3){
            System.out.print("Enter name: ");
            name = sc.nextLine();
            System.out.print("Enter phone: ");
            phone = sc.nextLine();   
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
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice");
            }
        }
    }
}

    
class DoublyLinkedList {
    Node head;

    void insert(Contact contact) {
        Node newNode = new Node(contact);

        if (head == null) {
            head = newNode;
            return;
        }

        Node temp = head;
        while (temp.next != null) {
            temp = temp.next;
        }

        temp.next = newNode;
        newNode.prev = temp;
    }
        void display() {
        Node temp = head;

        while (temp != null) {
            System.out.println(temp.data.name + " - " + temp.data.phone);
            temp = temp.next;
        }
    }
        Node search(String name) {
        Node temp = head;

        while (temp != null) {
            if (temp.data.name.equalsIgnoreCase(name)) {
                return temp;
            }
            temp = temp.next;
        }

        return null;
    }
}
