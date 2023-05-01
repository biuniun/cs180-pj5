import java.util.ArrayList;

public class Seller extends User {
    public Seller(String email) {
        super(email, Roles.Seller);
    }

    @Override
    public ArrayList<Message> getConversation(User user) {
        return new ArrayList<>(super.getHistory().stream()
                .filter(m -> m.getCustomer().equals(user))
                .filter(m -> m.getSeller().equals(this))
                .filter(m -> m.isSellerVis()).toList());
    }


    @Override
    public String toString() {
        return this.getEmail();
    }
}