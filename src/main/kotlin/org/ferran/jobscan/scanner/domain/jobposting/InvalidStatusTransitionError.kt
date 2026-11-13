package org.ferran.jobscan.scanner.domain.jobposting

class InvalidStatusTransitionError(val from: PostingStatus, val to: PostingStatus) :
    RuntimeException("Cannot change status from $from to $to")
