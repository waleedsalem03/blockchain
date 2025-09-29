import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

class Transaction {
    String from;
    String to;
    double amount;
    String timestamp;

    Transaction(String from, String to, double amount) {
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.timestamp = Instant.now().toString();
    }

    String compact() {
        return from + "->" + to + ":" + amount + "@" + timestamp;
    }

    public String toString() {
        return from + " -> " + to + " : " + amount + " at " + timestamp;
    }
}

class Block {
    int index;
    String timestamp;
    String prevHash;
    List<Transaction> transactions;
    String hash;

    Block(int index, String prevHash, List<Transaction> txs) {
        this.index = index;
        this.timestamp = Instant.now().toString();
        this.prevHash = prevHash;
        this.transactions = new ArrayList<>(txs);
        this.hash = computeHash();
    }

    String computeHash() {
        try {
            MessageDigest d = MessageDigest.getInstance("SHA-256");
            StringBuilder sb = new StringBuilder();
            sb.append(index).append(timestamp).append(prevHash);
            for (Transaction t : transactions) sb.append(t.compact());
            byte[] h = d.digest(sb.toString().getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : h) hex.append(String.format("%02x", b));
            return hex.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String toString() {
        return "Block " + index + " | hash=" + hash.substring(0,16) + "...";
    }
}

public class CentralBlockchain {
    List<Block> chain = new ArrayList<>();
    List<Transaction> mempool = new ArrayList<>();

    CentralBlockchain() {
        Block genesis = new Block(0, "0", List.of(new Transaction("SYSTEM","SYSTEM",0)));
        chain.add(genesis);
    }

    void addTransaction(String from, String to, double amount) {
        mempool.add(new Transaction(from, to, amount));
    }

    void createBlock() {
        if (mempool.isEmpty()) return;
        String prev = chain.get(chain.size()-1).hash;
        Block b = new Block(chain.size(), prev, mempool);
        chain.add(b);
        mempool.clear();
    }

    void explorer() {
        for (Block b : chain) {
            System.out.println(b);
            for (Transaction t : b.transactions) System.out.println("  " + t);
        }
    }

    public static void main(String[] args) {
        CentralBlockchain bc = new CentralBlockchain();
        bc.addTransaction("Omar", "Sara", 10);
        bc.addTransaction("Sara", "Ali", 3.5);
        bc.createBlock();
        bc.addTransaction("Ali", "Omar", 1.2);
        bc.createBlock();
        bc.explorer();
    }
}
