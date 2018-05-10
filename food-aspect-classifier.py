from pymongo import MongoClient

client = MongoClient('localhost', 27017)

db = client.nlp

reviews = db.foodsentiment.find()

rawFoodLabel = []
textTrain = []

for review in reviews:
	rawFoodLabel.append(review.get('foodPolarityInteger'))
	textTrain.append(review.get('translatedText'))

foodLabel = []

# Target Label
# 1 --> 100
# 0 --> 010
# -1--> 001

for labelnya in rawFoodLabel:
	if(labelnya == 1):
		foodLabel.append([1,0,0])
	elif(labelnya == 0):
		foodLabel.append([0,1,0])
	else:
		foodLabel.append([0,0,1])

from keras.preprocessing.text import one_hot
from keras.preprocessing.sequence import pad_sequences
import numpy as np

# integer encode the documents
vocab_size = 200
encoded_docs = [one_hot(d, vocab_size) for d in textTrain]

max_length = 200
padded_docs = pad_sequences(encoded_docs, maxlen=max_length, padding='post')
apocal = np.array(encoded_docs)

from keras.models import Sequential
from keras.layers import Dense
from keras.layers import Flatten
from keras.layers import LSTM
from keras.layers.embeddings import Embedding

model = Sequential()
model.add(Embedding(vocab_size, 32, input_length=max_length))
model.add(Flatten())
model.add(Dense(max_length, activation='relu', kernel_initializer='glorot_uniform'))
model.add(Dense(max_length, activation='relu', kernel_initializer='glorot_uniform'))
model.add(Dense(max_length, activation='relu', kernel_initializer='glorot_uniform'))
model.add(Dense(3, activation='softmax'))
model.compile(optimizer='adam', loss='categorical_crossentropy', metrics=['acc'])
print(model.summary())

counter = 0
akurasi = 0

while (counter<10) and (akurasi<1):
	model.fit(padded_docs, np.array(foodLabel), epochs=100, verbose=0, batch_size=3000)
	loss,akurasi = model.evaluate(np.array(padded_docs), np.array(foodLabel))
	print("Training Accuracy: ",akurasi)
	print("Current Counter: ",counter)
	counter+=1

nilaiAkurasi = 0
classifierResults = model.predict(padded_docs)

print("Food Label Size: ",len(foodLabel), " Classifier Results: ", len(classifierResults))

import operator

# True Positive --> Identified as POSITIVE polarity
tp = 0
tn = 0
fp = 0
fn = 0

for x in range(len(foodLabel)):
	foodFromData = foodLabel[x]
	rawFoodFromClassifier = classifierResults[x]
	index, value = max(enumerate(rawFoodFromClassifier), key=operator.itemgetter(1))
	foodFromClassifier = None
	if(index == 0):
		foodFromClassifier = [1,0,0]
	elif(index == 1):
		foodFromClassifier = [0,1,0]
	else:
		foodFromClassifier = [0,0,1]
	if(foodFromData == foodFromClassifier):
		if(foodFromClassifier == [1,0,0]):
			tp+=1
		else:
			tn+=1
		nilaiAkurasi+=1
	else:
		if(foodFromClassifier == [1,0,0]):
			fp+=1
		else:
			fn+=1

print("Accuracy: ",(nilaiAkurasi/len(foodLabel))*100,"%")
print("Precision: ", tp/(tp+fp))
print("Recall: ", tp/(tp+fn))
print("F1: ", (2*tp)/((2*tp)+fp+fn))