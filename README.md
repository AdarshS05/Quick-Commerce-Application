This project aims to develop a quick commerce android application. It uses the core concepts in android such as UI controls, layouts, different types of views, intents, services and SQLite for local storage. 

In this application customers can:
*	View available products 
*	Add products to cart and checkout
*	Choose delivery location manually or via GPS
*	Receive notification alerts for status of upcoming deliveries
*	View order history and give feedback for completed orders
 
# Features Implemented
## User Management and Authetication
*	Splash screen: initial loading activity for the application which displays the app name and logo along with a progress bar. Routing is done based on the setting of a valid session flag. If a valid session flag exists in the Shared Preferences, the user is redirected to the Main Activity else they are redirected to the Login Activity. 
*	Registration screen: a form where users can enter their username, email, password written to local storage. Only registered individuals can log into the application
*	Login screen: validates the username password against the stored credentials. On successful authentication a login flag is set in the shared preferences and explicit intent is sent to Main Activity. On failure a toast message is shown
*	Password Security and Utilities: passwords are hashed using SHA-256 with public salting before persistence. A shared constants file is used for the utilities that are used throughout in the application
Product Catalog and Search
*	Live REST API integration: product data is fetched from the FakeStore REST API using a Volley request queue. Fetched results are parsed into Product objects and synced into local SQLite products 
*	Search: a search bar in the global header uses auto complete text view to give matches against products and categories and filters the product grid
*	Home screen displays a list of all available products using a recycler view bound via a ProductAdapter where each product has a product card, name, price, add to cart button and a glide-loaded image
*	Product details screen: displays product information when clicking on any product in the home screen. Has an option to add and subtract the quantity

## Cart and Checkout
*	Cart screen: lists current cart items in a recycler view, where each item supports increment, decrement and removal with changes written directly to the SQLite cart. A summary with the subtotal, delivery fee and total amount is displayed along with a button to proceed to checkout
*	Checkout screen: text field and button to select delivery address, radio group to choose payment method (cash or UPI), button to place order if the delivery address is set.
*	Order status and feedback: a live-order status view with a rate this order action and a feedback form with a rating bar with an optional comment field submitted after delivery
  
## Location Services (GPS and Google Maps)
*	Map Selection Screen: Uses Google Maps SDK for rendering an interactive map and selecting a delivery location which resolves the pinned location into a human readable address line
*	GPS is used for current location detection with runtime permission check (ACCESS_FINE_LOCATION) 
*	Background order-tracking service: a foreground services that steps an order through  its four stages using a manual timer updating the orders table and broadcasting each change locally and pushing a system notification using a Notification Manager when the app isn’t in the foreground


## Profile, Order History and Support
*	Profile screen: shows the logged in user’s name, email address with a logout action that clears the session flag, and also display order history of that particular user
*	Order History: same as what is displayed in the profile page sourced from the orders SQLite table each with a view details which displays the Order Details screen
*	FAQ screen: answers common questions related to the application using a simple list view
