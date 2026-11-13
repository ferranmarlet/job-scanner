package org.ferran.jobscan.scanner.domain.resume

interface ResumeStorage {
    fun store(fileName: String, content: ByteArray)
}
