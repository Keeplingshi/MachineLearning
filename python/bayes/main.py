from numpy import *

import bayes

# listOPosts,listClasses=bayes.loadDataSet()
# myVocabList=bayes.createVocabList(listOPosts)
#
# trainMat=[]
# for postinDoc in listOPosts:
#     trainMat.append(bayes.setOfWords2Vec(myVocabList,postinDoc))

# print(trainMat)
# print(listClasses)

# p0V,p1V,pAb=bayes.trainNB0(trainMat,listClasses)

# print(pAb)
# print(p0V)
# print(p1V)

# a=bayes.setOfWords2Vec(myVocabList,listOPosts[0])
# print(listOPosts)
# print(myVocabList)
# print(a)

bayes.spamTest()
