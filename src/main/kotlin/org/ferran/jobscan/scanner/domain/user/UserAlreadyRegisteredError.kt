package org.ferran.jobscan.scanner.domain.user

class UserAlreadyRegisteredError(externalId: String) :
    RuntimeException("A user with external id $externalId is already registered")
