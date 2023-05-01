import java.util.ArrayList;

public class Customer extends User {
    public Customer(String username) {
        super(username, Roles.Customer);
    }

    @Override
    public ArrayList<Message> getConversation(User user) {
        return new ArrayList<>(super.getHistory().stream()
                .filter(m -> m.getSeller().equals(user))
                .filter(m -> m.getCustomer().equals(this))
                .filter(m -> m.isCustomerVis()).toList());
    }

    @Override
    public String toString() {
        return this.getEmail();
    }
	
}