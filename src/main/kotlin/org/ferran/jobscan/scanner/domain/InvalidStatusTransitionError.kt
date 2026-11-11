package org.ferran.jobscan.scanner.domain

class InvalidStatusTransitionError(val from: PostingStatus, val to: PostingStatus) :
    RuntimeException("Cannot change status from $from to $to")
