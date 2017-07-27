# Braintree SDK demo

This project demonstrates an exploration of how to integrate the Braintree SDK into a project 
using clean architecture practices. The purpose of this project is not to provide a (fully) 
working solution, but rather demonstrate how one may be achieved.

The main components of the flow utilizes the following dependencies:
- RxJava 2 (from RxAndroid)
- RetroFit 2
- OkHttp 3

As this is not intended to be a fully structured project, the more complicated parts of clean 
architecture solutions have been left out (e.g., dependency injection). This may still be 
expanded up on the future, if so desired.

### Side note

For security reasons and the such, this currently uses the credentials used in the Braintree SDK 
demo (linked below). Please always keep your API keys / tokens secure. 

# Further reading

- [Braintree SDK guide](https://developers.braintreepayments.com/guides/client-sdk/setup/android/v2) -
Full guide on how to integrate Braintree into an Android project
- [Braintree SDK repo](https://github.com/braintree/braintree_android) - Source for the Braintree
SDK, including a working demo
