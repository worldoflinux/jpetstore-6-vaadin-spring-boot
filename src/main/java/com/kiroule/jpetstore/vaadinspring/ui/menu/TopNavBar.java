package com.kiroule.jpetstore.vaadinspring.ui.menu;

import static com.kiroule.jpetstore.vaadinspring.ui.util.ViewConfigUtil.getDisplayName;
import static java.lang.String.format;

import com.kiroule.jpetstore.vaadinspring.ui.event.UIEventBus;
import com.kiroule.jpetstore.vaadinspring.ui.event.UILogoutEvent;
import com.kiroule.jpetstore.vaadinspring.ui.event.UINavigationEvent;
import com.kiroule.jpetstore.vaadinspring.ui.form.SigninForm;
import com.kiroule.jpetstore.vaadinspring.ui.theme.JPetStoreTheme;
import com.kiroule.jpetstore.vaadinspring.ui.util.NavBarButtonUpdater;
import com.kiroule.jpetstore.vaadinspring.ui.view.AccountView;
import com.kiroule.jpetstore.vaadinspring.ui.view.CartView;
import com.kiroule.jpetstore.vaadinspring.ui.view.HelpView;
import com.kiroule.jpetstore.vaadinspring.ui.view.SearchView;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.Page;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;

import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

/**
 * @author Igor Baiborodine
 */
@SpringComponent
@UIScope
public class TopNavBar extends CssLayout {

  private static final long serialVersionUID = -4572532480213767784L;

  public static final String SIGNIN_BUTTON_URI = "sign-in";
  public static final String SIGNOUT_BUTTON_URI = "sign-out";

  @Autowired
  private NavBarButtonUpdater navBarButtonUpdater;
  @Autowired
  private SigninForm signinForm;

  private Button signinButton;
  private Label userLabel;
  private Button signoutButton;

  @PostConstruct
  void init() {

    addStyleName(JPetStoreTheme.MENU_ROOT);
    addStyleName(JPetStoreTheme.TOP_MENU);

    final TextField searchTextField = new TextField();
    searchTextField.setImmediate(true);
    searchTextField.addShortcutListener(new ShortcutListener("enter-shortcut", ShortcutAction.KeyCode.ENTER, null) {
      private static final long serialVersionUID = 4638926023595229738L;
      @Override
      public void handleAction(Object sender, Object target) {
        searchProducts(((TextField) target).getValue());
      }
    });
    addComponent(searchTextField);

    addButton(SearchView.VIEW_NAME, getDisplayName(SearchView.class),
        event -> searchProducts(searchTextField.getValue()));
    addButton(CartView.VIEW_NAME, getDisplayName(CartView.class));
    addButton(AccountView.VIEW_NAME, getDisplayName(AccountView.class));
    signinButton = addButton(SIGNIN_BUTTON_URI, "Sing in", event -> {
      navBarButtonUpdater.setButtonSelected(SIGNIN_BUTTON_URI);
      signinForm.openInModalWidow();
    });
    signoutButton = addButton(SIGNOUT_BUTTON_URI, "Sign out", event -> {
      navBarButtonUpdater.setButtonSelected(SIGNOUT_BUTTON_URI);
      signoutButton.setVisible(false);
      signinButton.setVisible(true);
      UIEventBus.post(new UILogoutEvent());
    });
    signoutButton.setVisible(false);
    addButton(HelpView.VIEW_NAME, getDisplayName(HelpView.class));

    userLabel = new Label();
    userLabel.addStyleName(JPetStoreTheme.WELCOME_USER_LABEL);
    addComponent(userLabel);
  }

  private void searchProducts(String keyword) {

    if (keyword.trim().length() < 3) {
      new Notification("Keyword length must be greater than 2", null, Notification.Type.WARNING_MESSAGE)
          .show(Page.getCurrent());
    } else {
      String uri = SearchView.VIEW_NAME + "/" + keyword.trim().toLowerCase().replaceAll("%", "");
      UIEventBus.post(new UINavigationEvent(uri));
    }
  }

  private Button addButton(String uri, String caption) {
    return addButton(uri, caption, event -> UIEventBus.post(new UINavigationEvent(uri)));
  }

  private Button addButton(String uri, String caption, Button.ClickListener listener) {

    Button button = new Button(caption, listener);
    button.addStyleName(JPetStoreTheme.MENU_ITEM);
    button.addStyleName(JPetStoreTheme.BUTTON_BORDERLESS);
    navBarButtonUpdater.mapButtonToUri(uri, button);
    addComponent(button);
    return button;
  }

  public void updateUserLabel(String firstName) {
    userLabel.setValue(format("Hello, %s!", firstName));
  }
}