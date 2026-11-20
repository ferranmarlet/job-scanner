package org.ferran.jobscan.scanner.domain.user

class UserEmailUnavailableError :
    RuntimeException("The identity provider did not return an email for the user")
