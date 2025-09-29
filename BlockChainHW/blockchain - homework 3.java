import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;




class Block {
    int index;
    String timestamp;
    String data;
    String prevHash;
    String hash;

    Block(int index, String data, String prevHash) {
        this.index = index;
        this.timestamp = Instant.now().toString();
        this.data = data;
        this.prevHash = prevHash;
        this.hash = computeHash();
    }

    String computeHash() {
        try {
            MessageDigest d = MessageDigest.getInstance("SHA-256");
            String s = index + timestamp + data + prevHash;
            byte[] h = d.digest(s.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : h) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String toString() {
        return "Block " + index + " | " + data + " | " + hash.substring(0,10) + "...";
    }
}

class Blockchain {
    List<Block> chain = new ArrayList<>();

    Blockchain() {
        chain.add(new Block(0, "Genesis", "0"));
    }

    void addBlock(String data) {
        Block prev = chain.get(chain.size()-1);
        Block b = new Block(chain.size(), data, prev.hash);
        chain.add(b);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Block b : chain) sb.append(b).append("\n");
        return sb.toString();
    }
}

class Node {
    Blockchain blockchain = new Blockchain();

    void receiveBlock(Block b) {
        Block last = blockchain.chain.get(blockchain.chain.size()-1);
        if (b.prevHash.equals(last.hash)) blockchain.chain.add(b);
    }
}

public class DecentralizedBlockchain {
    public static void main(String[] args) {
        Node node1 = new Node();
        Node node2 = new Node();
        Node node3 = new Node();

        Block b1 = new Block(1, "Omar sends 10 coins to Sara", node1.blockchain.chain.get(0).hash);
        node1.blockchain.chain.add(b1);
        node2.receiveBlock(b1);
        node3.receiveBlock(b1);

        Block b2 = new Block(2, "Sara sends 5 coins to Ali", node1.blockchain.chain.get(1).hash);
        node1.blockchain.chain.add(b2);
        node2.receiveBlock(b2);
        node3.receiveBlock(b2);

        System.out.println("Node1:\n" + node1.blockchain);
        System.out.println("Node2:\n" + node2.blockchain);
        System.out.println("Node3:\n" + node3.blockchain);
    }
}



