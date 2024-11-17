export interface Recipe {
  id: string;

   title: string;
   description: string;
   date: Date;

   categories: string[];
   ingredients: string[];
   directions: string[];

   calories: number;
   fat: number;
   protein: number;
   rating: number;
   sodium: number;
}
