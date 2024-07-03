# android-callbutton-example

Example app to show how to use ExolveVoiceSDK only for outgoing calls.

### Highlights

- To initialize SDK use `Communicator.initializeForOutgoingCalls(context, configuration)`.
- Call `CallClient.register(login, password)` before making calls. It will only initialize the user agent without actual SIP registration.
- Provide `ICallsListener` implementation to handle calls events.
- Implementing `IRegistrationListener` is not required in this mode, but still can be done. Note that after activation registration state will be `OFFLINE` instead of `REGISTERED`.