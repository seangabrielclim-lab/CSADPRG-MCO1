check_user <- function(name, users){
    for (user in users){
        if (tolower(name) == tolower(user)){
            return (TRUE)        
        }
    }
    return (FALSE)
}

get_user_index <- function(name, user){
    currentIndex <- 1
    for (user in users){
        if (tolower(name) == tolower(user)){
            return (currentIndex)
        }
        currentIndex <- currentIndex + 1
    }
    return (-1)
}